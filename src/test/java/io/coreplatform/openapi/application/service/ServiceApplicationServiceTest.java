package io.coreplatform.openapi.application.service;

import io.coreplatform.openapi.application.command.CreateServiceCommand;
import io.coreplatform.openapi.application.domain.ApiService;
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
class ServiceApplicationServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    private ServiceApplicationService service;

    @BeforeEach
    void setUp() {
        service = new ServiceApplicationService(serviceRepository);
    }

    @Test
    void shouldCreateService() {
        CreateServiceCommand cmd = new CreateServiceCommand();
        cmd.setServiceName("用户服务");
        cmd.setServiceCode("core-user");
        cmd.setDescription("用户中心服务");

        when(serviceRepository.existsByServiceCode("core-user")).thenReturn(false);
        when(serviceRepository.save(any(ApiService.class))).thenAnswer(inv -> {
            ApiService s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        ApiService result = service.createService(cmd);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("core-user", result.getServiceCode());
        assertEquals("ACTIVE", result.getStatus());
        assertEquals("1.0", result.getVersion());
    }

    @Test
    void shouldRejectDuplicateServiceCode() {
        CreateServiceCommand cmd = new CreateServiceCommand();
        cmd.setServiceName("测试");
        cmd.setServiceCode("core-user");

        when(serviceRepository.existsByServiceCode("core-user")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.createService(cmd));
    }

    @Test
    void shouldFindById() {
        ApiService s = ApiService.builder().id(1L).serviceName("Test").serviceCode("test").status("ACTIVE").build();
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(s));

        Optional<ApiService> result = service.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("test", result.get().getServiceCode());
    }
}