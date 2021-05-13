package com.polykhel.billnext.service.impl

import com.polykhel.billnext.domain.Activity
import com.polykhel.billnext.repository.ActivityRepository
import com.polykhel.billnext.security.ADMIN
import com.polykhel.billnext.security.userIsCurrentUser
import com.polykhel.billnext.service.ActivityService
import com.polykhel.billnext.service.dto.ActivityDTO
import com.polykhel.billnext.service.mapper.ActivityMapper
import com.polykhel.billnext.web.rest.errors.ForbiddenException
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Service Implementation for managing [Activity].
 */
@Service
@Transactional
class ActivityServiceImpl(
    private val activityRepository: ActivityRepository,
    private val activityMapper: ActivityMapper
) : ActivityService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun save(activityDTO: ActivityDTO): ActivityDTO {
        log.debug("Request to save Activity : $activityDTO")

        if (!userIsCurrentUser(activityDTO.user?.login)) {
            throw ForbiddenException()
        }

        var activity = activityMapper.toEntity(activityDTO)
        activity = activityRepository.save(activity)
        return activityMapper.toDto(activity)
    }

    override fun partialUpdate(activityDTO: ActivityDTO): Optional<ActivityDTO> {
        log.debug("Request to partially update Activity : {}", activityDTO)

        val activity = activityRepository.findById(activityDTO.id!!)

        if (activity.isPresent && !userIsCurrentUser(activity.get().user?.login)) {
            throw ForbiddenException()
        }

        return activity
            .map {
                activityMapper.partialUpdate(it, activityDTO)
                it
            }
            .map { activityRepository.save(it) }
            .map { activityMapper.toDto(it) }
    }

    @Secured(ADMIN)
    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<ActivityDTO> {
        log.debug("Request to get all Activities")
        return activityRepository.findAll(pageable)
            .map(activityMapper::toDto)
    }

    @Transactional(readOnly = true)
    override fun findAllByCurrentUser(pageable: Pageable): Page<ActivityDTO> {
        log.debug("Request to get all Activities by currentUser")
        return activityRepository.findByUserIsCurrentUser(pageable)
            .map(activityMapper::toDto)
    }

    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<ActivityDTO> {
        log.debug("Request to get Activity : $id")
        val activity = activityRepository.findById(id)

        if (activity.isPresent && !userIsCurrentUser(activity.get().user?.login)) {
            throw ForbiddenException()
        }

        return activity
            .map(activityMapper::toDto)
    }

    override fun delete(id: Long) {
        log.debug("Request to delete Activity : $id")

        activityRepository.findById(id).ifPresent {
            if (!userIsCurrentUser(it.user?.login)) {
                throw ForbiddenException()
            }

            activityRepository.deleteById(id)
        }
    }
}
