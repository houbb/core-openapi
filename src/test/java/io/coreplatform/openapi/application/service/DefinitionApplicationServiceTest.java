package io.coreplatform.openapi.application.service;

import io.coreplatform.openapi.application.command.CreateDefinitionCommand;
import io.coreplatform.openapi.application.domain.ApiService;
import io.coreplatform.openapi.application.domain.Definition;
import io.coreplatform.openapi.application.port.DefinitionRepository;
import io.coreplatform.openapi.application.port.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefinitionApplicationServiceTest {

    @Mock
    private DefinitionRepository definitionRepository;
    @Mock
    private ServiceRepository serviceRepository;

    private DefinitionApplicationService service;

    @BeforeEach
    void setUp() {
        service = new DefinitionApplicationService(definitionRepository, serviceRepository);
    }

    @Test
    void shouldCreateDefinitionAsDraft() {
        // Mock service exists
        io.coreplatform.openapi.application.domain.ApiService svc =
                io.coreplatform.openapi.application.domain.ApiService.builder().id(1L).serviceCode("core-user").build();
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(svc));

        CreateDefinitionCommand cmd = new CreateDefinitionCommand();
        cmd.setServiceId(1L);
        cmd.setName("getUser");
        cmd.setPath("/users/{id}");
        cmd.setHttpMethod("get"); // lowercase should be uppercased

        when(definitionRepository.save(any(Definition.class))).thenAnswer(inv -> {
            Definition d = inv.getArgument(0);
            d.setId(1L);
            return d;
        });

        Definition result = service.createDefinition(cmd);
        assertEquals("DRAFT", result.getStatus());
        assertEquals("GET", result.getHttpMethod());
        assertEquals("/users/{id}", result.getPath());
    }

    @Test
    void shouldPublishFromDraft() {
        Definition draft = Definition.builder().id(1L).status("DRAFT").serviceId(1L).build();
        when(definitionRepository.findById(1L)).thenReturn(Optional.of(draft));
        when(definitionRepository.save(any(Definition.class))).thenAnswer(inv -> inv.getArgument(0));

        Definition result = service.publish(1L);
        assertEquals("PUBLISHED", result.getStatus());
    }

    @Test
    void shouldNotPublishFromOffline() {
        Definition offline = Definition.builder().id(1L).status("OFFLINE").serviceId(1L).build();
        when(definitionRepository.findById(1L)).thenReturn(Optional.of(offline));

        assertThrows(IllegalStateException.class, () -> service.publish(1L));
    }

    @Test
    void shouldNotDeprecateFromDraft() {
        Definition draft = Definition.builder().id(1L).status("DRAFT").serviceId(1L).build();
        when(definitionRepository.findById(1L)).thenReturn(Optional.of(draft));

        assertThrows(IllegalStateException.class, () -> service.deprecate(1L));
    }
}
