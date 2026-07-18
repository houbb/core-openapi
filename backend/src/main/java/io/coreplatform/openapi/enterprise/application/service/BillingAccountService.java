package io.coreplatform.openapi.enterprise.application.service;

import io.coreplatform.openapi.enterprise.domain.BillingAccount;
import io.coreplatform.openapi.enterprise.port.BillingAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BillingAccountService {

    private final BillingAccountRepository billingAccountRepository;

    public BillingAccount findById(Long id) {
        return billingAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("计费账户不存在: " + id));
    }

    public BillingAccount findByOrganizationId(Long organizationId) {
        return billingAccountRepository.findByOrganizationId(organizationId).orElse(null);
    }

    @Transactional
    public BillingAccount createOrGet(Long organizationId, String accountName, String currency) {
        BillingAccount existing = billingAccountRepository.findByOrganizationId(organizationId).orElse(null);
        if (existing != null) {
            return existing;
        }

        BillingAccount account = BillingAccount.builder()
                .organizationId(organizationId)
                .accountName(accountName != null ? accountName : "Default Account")
                .balance(BigDecimal.ZERO)
                .currency(currency != null ? currency : "CNY")
                .status("ACTIVE")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .createUser("system")
                .updateUser("system")
                .build();
        return billingAccountRepository.save(account);
    }

    @Transactional
    public BillingAccount updateStatus(Long id, String status) {
        BillingAccount account = findById(id);
        account.setStatus(status);
        account.setUpdateTime(LocalDateTime.now());
        return billingAccountRepository.save(account);
    }

    @Transactional
    public void deleteBillingAccount(Long id) {
        billingAccountRepository.deleteById(id);
    }
}