package io.coreplatform.openapi.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.application.command.CreateServiceCommand;
import io.coreplatform.openapi.application.domain.ApiService;
import io.coreplatform.openapi.application.port.ServiceRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceApplicationService {

    private final ServiceRepository serviceRepository;

    @org.springframework.transaction.annotation.Transactional
    public ApiService createService(CreateServiceCommand command) {
        if (serviceRepository.existsByServiceCode(command.getServiceCode())) {
            throw new IllegalArgumentException("服务编码已存在: " + command.getServiceCode());
        }
        ApiService apiService = ApiService.builder()
                .serviceName(command.getServiceName())
                .serviceCode(command.getServiceCode())
                .description(command.getDescription())
                .owner(command.getOwner())
                .version(command.getVersion() != null ? command.getVersion() : "1.0")
                .status("ACTIVE")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .createUser("system")
                .updateUser("system")
                .build();
        return serviceRepository.save(apiService);
    }

    @org.springframework.transaction.annotation.Transactional
    public ApiService updateService(Long id, CreateServiceCommand command) {
        ApiService existing = serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("服务不存在: " + id));

        if (!existing.getServiceCode().equals(command.getServiceCode())
                && serviceRepository.existsByServiceCode(command.getServiceCode())) {
            throw new IllegalArgumentException("服务编码已存在: " + command.getServiceCode());
        }

        existing.setServiceName(command.getServiceName());
        existing.setServiceCode(command.getServiceCode());
        existing.setDescription(command.getDescription());
        existing.setOwner(command.getOwner());
        if (command.getVersion() != null) {
            existing.setVersion(command.getVersion());
        }
        existing.setUpdateTime(LocalDateTime.now());
        existing.setUpdateUser("system");
        return serviceRepository.save(existing);
    }

    public Optional<ApiService> findById(Long id) {
        return serviceRepository.findById(id);
    }

    public IPage<ApiService> findPage(long page, long size, String keyword) {
        return serviceRepository.findPage(page, size, keyword);
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }
}