/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.device;

import com.papercut.dust.model.Repository;
import com.papercut.dust.model.TransactionalMapProducer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Collection;

import static java.util.stream.Collectors.joining;

/**
 * Repository of devices.
 */
@ApplicationScoped
public class DeviceRepository extends Repository<Device> {

    DeviceRepository() {
        super(null, null);
    }

    @Inject
    public DeviceRepository(final TransactionalMapProducer storeProducer) {
        super(Device.class, storeProducer.createStore("devices"));
    }

    void loadTestData(final boolean update) {
        load("devices", update);
    }

    @Override
    protected boolean hasKeyword(final Device d, final String keyword) {
        return containsKeyword(d.brand, keyword)
                || containsKeyword(d.model, keyword)
                || containsKeyword(d.nickname, keyword)
                || containsKeyword(d.hostname, keyword)
                || containsKeyword(d.ip, keyword)
                || containsKeyword(d.location, keyword);
    }

    @Override
    protected Collection<Device> findByKeyword(final String keyword) {
        return super.findByKeyword(keyword);
    }

    public Device findUniqueByKeyword(final String keyword) {
        final Collection<Device> foundDevices = findByKeyword(keyword);
        switch (foundDevices.size()) {
            case 0:
                throw NoSuchUniqueDeviceException.none(keyword);
            case 1:
                return foundDevices.iterator().next();
            default:
                final String foundDeviceNames = foundDevices.stream().map(Device::getNickname).collect(joining("\n"));
                throw NoSuchUniqueDeviceException.tooMany(keyword, foundDeviceNames);
        }
    }

    @Override
    public Device findById(final long id) {
        return super.findById(id);
    }

    @Override
    public Collection<Device> findAll() {
        return super.findAll();
    }

    @Override
    protected Device update(final Device entity) {
        return super.update(entity);
    }

    @Override
    protected Device create(final Device entity) {
        return super.create(entity);
    }

    @Override
    protected Device delete(final long id) {
        return super.delete(id);
    }

    public void handleDeviceEvent(@Observes final DeviceEvent event) {
        switch (event.type) {
            case CLAIMED:
                updateClaimDetails(event.claim.deviceId, event.claim.userId, event.claim.username);
                break;
            case UNCLAIMED:
                updateClaimDetails(event.claim.deviceId, null, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown event: " + event.type);
        }
    }

    private void updateClaimDetails(final long deviceId, final Long userId, final String username) {
        final Device device = findById(deviceId);
        update(device.claimedBy(userId, username));
    }

    public Device findByName(final String nickname) {
        return store.values().stream().filter(e -> e.nickname.equals(nickname))
                .findFirst()
                .orElseThrow(() -> NoSuchUniqueDeviceException.none(nickname));
    }

}
