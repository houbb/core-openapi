package io.coreplatform.openapi.enterprise.application.service;

import io.coreplatform.openapi.enterprise.domain.Organization;
import io.coreplatform.openapi.enterprise.domain.Member;
import io.coreplatform.openapi.enterprise.domain.Partner;
import io.coreplatform.openapi.enterprise.domain.Contract;
import io.coreplatform.openapi.enterprise.port.OrganizationRepository;
import io.coreplatform.openapi.enterprise.port.MemberRepository;
import io.coreplatform.openapi.enterprise.port.PartnerRepository;
import io.coreplatform.openapi.enterprise.port.ContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnterpriseServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PartnerRepository partnerRepository;
    @Mock
    private ContractRepository contractRepository;

    private OrganizationService organizationService;
    private MemberService memberService;
    private PartnerService partnerService;
    private ContractService contractService;

    @BeforeEach
    void setUp() {
        organizationService = new OrganizationService(organizationRepository);
        memberService = new MemberService(memberRepository);
        partnerService = new PartnerService(partnerRepository);
        contractService = new ContractService(contractRepository);
    }

    // ==================== Organization Tests ====================

    @Test
    void shouldCreateOrganizationWithDefaults() {
        when(organizationRepository.save(any(Organization.class))).thenAnswer(inv -> {
            Organization o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });

        Organization result = organizationService.createOrganization("Test Corp", "ENTERPRISE", null, "", "", "", "");
        assertEquals("Test Corp", result.getName());
        assertEquals("ENTERPRISE", result.getType());
        assertEquals("ACTIVE", result.getStatus());
        assertNotNull(result.getCode());
        assertTrue(result.getCode().startsWith("ORG_"));
        assertNotNull(result.getTenantId());
        assertTrue(result.getTenantId().startsWith("tenant_"));
    }

    @Test
    void shouldFindOrganizationById() {
        Organization org = Organization.builder().id(1L).name("Test").status("ACTIVE").build();
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(org));

        Organization result = organizationService.findById(1L);
        assertEquals("Test", result.getName());
    }

    @Test
    void shouldThrowWhenOrganizationNotFound() {
        when(organizationRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> organizationService.findById(999L));
    }

    @Test
    void shouldUpdateOrganizationStatus() {
        Organization org = Organization.builder().id(1L).name("Test").status("ACTIVE").build();
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(org));
        when(organizationRepository.save(any(Organization.class))).thenAnswer(inv -> inv.getArgument(0));

        Organization result = organizationService.updateStatus(1L, "SUSPENDED");
        assertEquals("SUSPENDED", result.getStatus());
    }

    // ==================== Member Tests ====================

    @Test
    void shouldAddMemberToOrganization() {
        when(memberRepository.findByOrgAndUser(1L, 100L)).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> {
            Member m = inv.getArgument(0);
            m.setId(1L);
            return m;
        });

        Member result = memberService.addMember(1L, null, 100L, "MEMBER");
        assertEquals(1L, result.getOrganizationId());
        assertEquals(100L, result.getUserId());
        assertEquals("MEMBER", result.getRole());
        assertEquals("ACTIVE", result.getStatus());
        assertNotNull(result.getJoinedAt());
    }

    @Test
    void shouldPreventDuplicateMember() {
        Member existing = Member.builder().id(1L).organizationId(1L).userId(100L).build();
        when(memberRepository.findByOrgAndUser(1L, 100L)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> memberService.addMember(1L, null, 100L, "MEMBER"));
    }

    @Test
    void shouldUpdateMemberRole() {
        Member member = Member.builder().id(1L).organizationId(1L).role("MEMBER").build();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

        Member result = memberService.updateMemberRole(1L, "ADMIN");
        assertEquals("ADMIN", result.getRole());
    }

    @Test
    void shouldChangeMemberTeam() {
        Member member = Member.builder().id(1L).organizationId(1L).teamId(null).build();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

        Member result = memberService.changeTeam(1L, 5L);
        assertEquals(5L, result.getTeamId());
    }

    // ==================== Partner Tests ====================

    @Test
    void shouldCreatePartnerWithDefaults() {
        when(partnerRepository.save(any(Partner.class))).thenAnswer(inv -> {
            Partner p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        Partner result = partnerService.createPartner(1L, "Acme Inc", null, "John", "john@acme.com", "", "");
        assertEquals("Acme Inc", result.getName());
        assertEquals("STANDARD", result.getLevel());
        assertEquals("ACTIVE", result.getStatus());
    }

    @Test
    void shouldCreateStrategicPartner() {
        when(partnerRepository.save(any(Partner.class))).thenAnswer(inv -> {
            Partner p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        Partner result = partnerService.createPartner(1L, "Big Corp", "STRATEGIC", "", "", "", "");
        assertEquals("STRATEGIC", result.getLevel());
    }

    // ==================== Contract Tests ====================

    @Test
    void shouldCreateContractAsDraft() {
        when(contractRepository.save(any(Contract.class))).thenAnswer(inv -> {
            Contract c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        Contract result = contractService.createContract(1L, "CT-001", "Enterprise Plan",
                "Enterprise", LocalDate.now(), LocalDate.now().plusYears(1), 1000000L, 500, 1, 1, "");
        assertEquals("CT-001", result.getContractNo());
        assertEquals("DRAFT", result.getStatus());
        assertEquals(1000000L, result.getMaxRequests());
    }

    @Test
    void shouldActivateContract() {
        Contract draft = Contract.builder().id(1L).contractNo("CT-001").status("DRAFT").build();
        when(contractRepository.findById(1L)).thenReturn(Optional.of(draft));
        when(contractRepository.save(any(Contract.class))).thenAnswer(inv -> inv.getArgument(0));

        Contract result = contractService.activateContract(1L);
        assertEquals("ACTIVE", result.getStatus());
    }

    @Test
    void shouldExpireContract() {
        Contract active = Contract.builder().id(1L).contractNo("CT-001").status("ACTIVE").build();
        when(contractRepository.findById(1L)).thenReturn(Optional.of(active));
        when(contractRepository.save(any(Contract.class))).thenAnswer(inv -> inv.getArgument(0));

        Contract result = contractService.expireContract(1L);
        assertEquals("EXPIRED", result.getStatus());
    }

    // ==================== Integration Scenario Tests ====================

    @Test
    void fullEnterpriseWorkflow() {
        // 1. Create Organization
        when(organizationRepository.save(any(Organization.class))).thenAnswer(inv -> {
            Organization o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });
        Organization org = organizationService.createOrganization("My Corp", "ENTERPRISE", null, "", "", "", "");
        assertEquals("ACTIVE", org.getStatus());
        assertEquals("My Corp", org.getName());

        // 2. Add Members
        when(memberRepository.findByOrgAndUser(1L, 100L)).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> {
            Member m = inv.getArgument(0);
            m.setId(1L);
            return m;
        });
        Member member = memberService.addMember(1L, null, 100L, "ADMIN");
        assertEquals("ADMIN", member.getRole());

        // 3. Create Partner
        when(partnerRepository.save(any(Partner.class))).thenAnswer(inv -> {
            Partner p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });
        Partner partner = partnerService.createPartner(1L, "Partner Inc", "PREMIUM", "", "", "", "");
        assertEquals("PREMIUM", partner.getLevel());

        // 4. Create Contract
        when(contractRepository.save(any(Contract.class))).thenAnswer(inv -> {
            Contract c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });
        Contract contract = contractService.createContract(1L, "CT-002", "SaaS Plan",
                "Professional", LocalDate.now(), LocalDate.now().plusMonths(6), 500000L, 200, 1, 0, "");
        assertEquals("DRAFT", contract.getStatus());

        // 5. Activate Contract
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        Contract activated = contractService.activateContract(1L);
        assertEquals("ACTIVE", activated.getStatus());
    }
}