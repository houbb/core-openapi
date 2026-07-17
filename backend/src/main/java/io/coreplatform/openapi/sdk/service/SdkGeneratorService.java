package io.coreplatform.openapi.sdk.service;

import io.coreplatform.openapi.application.port.SdkGenerationRepository;
import io.coreplatform.openapi.application.port.SdkProjectRepository;
import io.coreplatform.openapi.sdk.domain.SdkGeneration;
import io.coreplatform.openapi.sdk.domain.SdkProject;
import io.coreplatform.openapi.sdk.spec.OpenApiSpecBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.CodegenConfigurator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SdkGeneratorService {

    private final SdkProjectRepository projectRepository;
    private final SdkGenerationRepository generationRepository;
    private final OpenApiSpecBuilder specBuilder;

    @Value("${app.sdk.output-dir:data/sdk}")
    private String sdkOutputDir;

    private static final String GENERATOR_VERSION = "7.8.0";

    /**
     * Generate SDK for selected APIs and language.
     */
    public SdkGeneration generate(String name, String language, String version, List<Long> apiIds) {
        // 1. Create project
        SdkProject project = SdkProject.builder()
                .name(name)
                .language(language)
                .version(version)
                .status("GENERATING")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        project = projectRepository.save(project);

        // 2. Create generation record
        SdkGeneration generation = SdkGeneration.builder()
                .sdkProjectId(project.getId())
                .apiIds(apiIds.toString())
                .generatorVersion(GENERATOR_VERSION)
                .status("GENERATING")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        generation = generationRepository.save(generation);

        // 3. Build OpenAPI spec and generate
        try {
            Path outputPath = Path.of(sdkOutputDir, "gen-" + generation.getId());
            Files.createDirectories(outputPath);

            // Build spec
            String specJson = specBuilder.buildSpec(apiIds, name);
            Path specFile = outputPath.resolve("openapi-spec.json");
            Files.writeString(specFile, specJson);
            log.info("OpenAPI spec written to {}", specFile);

            // Run OpenAPI Generator
            log.info("Generating {} SDK for project {}", language, project.getId());
            generateCode(specFile, outputPath.resolve("output"), language, name, version);

            // Package as zip
            Path zipPath = packageZip(outputPath.resolve("output"), generation.getId(), language);
            long fileSize = Files.size(zipPath);

            // Update generation record
            generation.setStatus("READY");
            generation.setDownloadUrl("sdk/gen-" + generation.getId() + ".zip");
            generation.setFileSize(fileSize);
            generation.setUpdateTime(LocalDateTime.now());
            generation = generationRepository.save(generation);

            // Update project
            project.setStatus("READY");
            project.setUpdateTime(LocalDateTime.now());
            projectRepository.save(project);

            log.info("SDK generation {} completed successfully, size={} bytes", generation.getId(), fileSize);

        } catch (Exception e) {
            log.error("SDK generation failed for project {}", project.getId(), e);
            generation.setStatus("FAILED");
            generation.setErrorMessage(e.getMessage());
            generation.setUpdateTime(LocalDateTime.now());
            generationRepository.save(generation);

            project.setStatus("FAILED");
            project.setErrorMessage(e.getMessage());
            project.setUpdateTime(LocalDateTime.now());
            projectRepository.save(project);
        }

        return generation;
    }

    private void generateCode(Path specFile, Path outputDir, String language, String name, String version) {
        CodegenConfigurator configurator = new CodegenConfigurator();
        configurator.setInputSpec(specFile.toAbsolutePath().toString());
        configurator.setOutputDir(outputDir.toAbsolutePath().toString());
        configurator.setGeneratorName(language);
        configurator.setPackageName("io.coreplatform.sdk");
        configurator.setApiPackage("io.coreplatform.sdk.api");
        configurator.setModelPackage("io.coreplatform.sdk.model");
        configurator.setInvokerPackage("io.coreplatform.sdk");

        // Additional properties
        configurator.addAdditionalProperty("projectName", name);
        configurator.addAdditionalProperty("projectVersion", version);
        configurator.addAdditionalProperty("hideGenerationTimestamp", "true");

        DefaultGenerator generator = new DefaultGenerator();
        generator.opts(configurator.toClientOptInput());
        generator.generate();
        log.info("OpenAPI Generator completed for language={}", language);
    }

    private Path packageZip(Path sourceDir, Long generationId, String language) throws IOException {
        Path zipPath = Path.of(sdkOutputDir, "gen-" + generationId + ".zip");
        Files.createDirectories(zipPath.getParent());

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))) {
            try (var files = Files.walk(sourceDir)) {
                files.filter(Files::isRegularFile).forEach(file -> {
                    try {
                        String entryName = sourceDir.relativize(file).toString().replace("\\", "/");
                        zos.putNextEntry(new ZipEntry(entryName));
                        Files.copy(file, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            }
        }

        return zipPath;
    }

    /**
     * Clean up old temporary generation directories.
     */
    public void cleanupTempDirs() {
        try {
            Path outputPath = Path.of(sdkOutputDir);
            if (Files.exists(outputPath)) {
                try (var dirs = Files.list(outputPath)) {
                    dirs.filter(Files::isDirectory)
                            .filter(d -> d.getFileName().toString().startsWith("gen-"))
                            .forEach(d -> {
                                try {
                                    Files.walk(d)
                                            .sorted(Comparator.reverseOrder())
                                            .forEach(p -> {
                                                try { Files.deleteIfExists(p); } catch (IOException ignored) {}
                                            });
                                } catch (IOException ignored) {}
                            });
                }
            }
        } catch (IOException e) {
            log.warn("Failed to cleanup temp dirs: {}", e.getMessage());
        }
    }
}
