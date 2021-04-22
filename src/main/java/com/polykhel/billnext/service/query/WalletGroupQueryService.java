package com.polykhel.billnext.service.query;

import com.polykhel.billnext.domain.User_;
import com.polykhel.billnext.domain.WalletGroup;
import com.polykhel.billnext.domain.WalletGroup_;
import com.polykhel.billnext.domain.Wallet_;
import com.polykhel.billnext.repository.WalletGroupRepository;
import com.polykhel.billnext.service.criteria.WalletGroupCriteria;
import com.polykhel.billnext.service.dto.WalletGroupDTO;
import com.polykhel.billnext.service.mapper.WalletGroupMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link WalletGroup} entities in the database.
 * The main input is a {@link WalletGroupCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link WalletGroupDTO} or a {@link Page} of {@link WalletGroupDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WalletGroupQueryService extends QueryService<WalletGroup> {

    private final Logger log = LoggerFactory.getLogger(WalletGroupQueryService.class);

    private final WalletGroupRepository walletGroupRepository;

    private final WalletGroupMapper walletGroupMapper;

    public WalletGroupQueryService(WalletGroupRepository walletGroupRepository, WalletGroupMapper walletGroupMapper) {
        this.walletGroupRepository = walletGroupRepository;
        this.walletGroupMapper = walletGroupMapper;
    }

    /**
     * Return a {@link List} of {@link WalletGroupDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<WalletGroupDTO> findByCriteria(WalletGroupCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<WalletGroup> specification = createSpecification(criteria);
        return walletGroupMapper.toDto(walletGroupRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link WalletGroupDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<WalletGroupDTO> findByCriteria(WalletGroupCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<WalletGroup> specification = createSpecification(criteria);
        return walletGroupRepository.findAll(specification, page).map(walletGroupMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WalletGroupCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<WalletGroup> specification = createSpecification(criteria);
        return walletGroupRepository.count(specification);
    }

    /**
     * Function to convert {@link WalletGroupCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<WalletGroup> createSpecification(WalletGroupCriteria criteria) {
        Specification<WalletGroup> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), WalletGroup_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), WalletGroup_.name));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(WalletGroup_.user, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getWalletsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getWalletsId(), root -> root.join(WalletGroup_.wallets, JoinType.LEFT).get(Wallet_.id))
                    );
            }
        }
        return specification;
    }
}
