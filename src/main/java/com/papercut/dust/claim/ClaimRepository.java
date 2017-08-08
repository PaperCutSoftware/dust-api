/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.claim;

import com.papercut.dust.model.TransactionalMapProducer;
import com.papercut.dust.device.DeviceEvent;
import com.papercut.dust.device.DeviceEvent.DeviceEventType;
import com.papercut.dust.model.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * Tracks claims.
 */
@ApplicationScoped
public class ClaimRepository extends Repository<Claim> {

    ClaimRepository() {
        super(null, null);
    }

    @Inject
    public ClaimRepository(final TransactionalMapProducer storeProducer) {
        super(Claim.class, storeProducer.createStore("claims"));
    }

    @Inject
    private Event<DeviceEvent> event;

    @Override
    protected boolean hasKeyword(final Claim claim, final String keyword) {
        return false;
    }

    @Override
    protected Collection<Claim> findByKeyword(final String keyword) {
        return super.findByKeyword(keyword);
    }

    @Override
    protected Claim findById(final long id) {
        return super.findById(id);
    }

    public Collection<Claim> find(final Long deviceId, final Long userId, final Boolean active) {
        return store.values().stream()
                .filter(c
                        -> (deviceId == null || c.deviceId == deviceId)
                        && (userId == null || c.userId == userId)
                        && (active == null || (active ? c.endDate == null : c.endDate.isBefore(LocalDateTime.now())))
                )
                .collect(toList());
    }

    public Claim findActive(final long deviceId, final long userId) throws NoSuchClaimException {
        return find(deviceId, userId, true).stream().findFirst().orElseThrow(() -> new NoSuchClaimException(
                String.format("There is no claim for device %s and user %s", deviceId, userId))
        );
    }

    public List<Claim> findActives(final long userId) {
        return new ArrayList<>(find(null, userId, true));
    }

    @Override
    protected Collection<Claim> findAll() {
        return super.findAll();
    }

    @Override
    public Claim update(final Claim claim) {
        return super.update(claim);
    }

    @Override
    public Claim create(final Claim claim) throws AlreadyClaimedException {
        final Collection<Claim> existingActiveClaims = find(claim.deviceId, null, true);
        if (!existingActiveClaims.isEmpty()) {
            final LocalDateTime now = LocalDateTime.now();
            throw existingActiveClaims.stream()
                    .filter(c
                            -> c.startDate.isBefore(now) && (c.endDate == null || c.endDate.isAfter(now))
                    )
                    .findFirst()
                    .map(AlreadyClaimedException::new)
                    .orElse(new AlreadyClaimedException(claim.deviceName));
        }

        final Claim createdClaim = super.create(claim.startingNow());

        event.fire(new DeviceEvent(createdClaim, DeviceEventType.CLAIMED));

        return createdClaim;
    }

    @Override
    public Claim delete(final long id) {
        final Claim deletedClaim = super.delete(id);
        if (deletedClaim != null) {
            event.fire(new DeviceEvent(deletedClaim, DeviceEventType.UNCLAIMED));
        }
        return deletedClaim;
    }
}
