package io.coreplatform.openapi.enterprise.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.enterprise.domain.Contract;

import java.util.List;
import java.util.Optional;

public interface ContractRepository {
    Contract save(Contract contract);
    Optional<Contract> findById(Long id);
    Optional<Contract> findByContractNo(String contractNo);
    IPage<Contract> findPage(long page, long size, Long organizationId, String status);
    List<Contract> findByOrganizationId(Long organizationId);
    void deleteById(Long id);
    Long countByStatus(String status);
}
