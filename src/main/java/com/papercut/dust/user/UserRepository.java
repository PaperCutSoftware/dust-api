/*
 * Copyright (c) 2017 PaperCut Software International Pty. Ltd.
 *
 * https://www.papercut.com
 *
 * Use of this source code is governed by an MIT license.
 * See the project's LICENSE file for more information.
 */
package com.papercut.dust.user;

import com.papercut.dust.model.Repository;
import com.papercut.dust.model.TransactionalMapProducer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

/**
 * Repository of users.
 */

@ApplicationScoped
public class UserRepository extends Repository<User> {

    private final Map<Long, List<Long>> watchedDevicesOfUser;
    private final Map<Long, List<Long>> watchersOfDevice;

    UserRepository() {
        super(null, null);
        this.watchedDevicesOfUser = null;
        this.watchersOfDevice = null;
    }

    @Inject
    public UserRepository(final TransactionalMapProducer storeProducer) {
        super(User.class, storeProducer.createStore("users"));
        watchedDevicesOfUser = storeProducer.createStore("watched-devices-per-user");
        watchersOfDevice = storeProducer.createStore("watchers-of-device");
    }

    public User findBySlackUsername(final String slackUsername) {
        return findAll().stream()
                .filter(u -> u.slackUsername != null && u.slackUsername.equals(slackUsername)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No such user"));
    }

    public User findByUsername(final String username) {
        return findAll().stream()
                .filter(u -> u.googleUserId.equals(username)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No such user"));
    }

    public Optional<User> findByGoogleUserId(final String googleUserId) {
        return findAll().stream()
                .filter(u -> u.googleUserId.equals(googleUserId))
                .findFirst();
    }

    @Override
    protected boolean hasKeyword(final User user, final String keyword) {
        return containsKeyword(user.slackUsername, keyword)
                || containsKeyword(user.googleUserId, keyword);
    }

    @Override
    protected Collection<User> findByKeyword(final String keyword) {
        return super.findByKeyword(keyword);
    }

    @Override
    protected User findById(final long id) {
        return super.findById(id);
    }

    @Override
    protected Collection<User> findAll() {
        return super.findAll();
    }

    @Override
    protected User update(final User user) {
        return super.update(user);
    }

    @Override
    protected User create(final User user) {
        return super.create(user);
    }

    public User createUser(final User user) {
        return findByGoogleUserId(user.googleUserId)
                .orElseGet(() -> create(user));
    }

    Collection<Long> findWatchesOfUser(final Long userId) {
        watchedDevicesOfUser.putIfAbsent(userId, Collections.emptyList());
        return watchedDevicesOfUser.get(userId);
    }

    public Collection<User> findWatchersOfDevice(final Long deviceId) {
        watchersOfDevice.putIfAbsent(deviceId, Collections.emptyList());
        return watchersOfDevice.get(deviceId).stream().map(this::findById).collect(toList());
    }

    void addWatch(final long deviceId, final long userId) {
        watchedDevicesOfUser.putIfAbsent(userId, Collections.emptyList());
        watchersOfDevice.putIfAbsent(deviceId, Collections.emptyList());

        watchedDevicesOfUser.put(userId, concat(of(deviceId), watchedDevicesOfUser.get(userId).stream())
                .collect(toList()));
        watchersOfDevice.put(deviceId, concat(of(userId), watchersOfDevice.get(deviceId).stream())
                .collect(toList()));
    }

    void removeWatch(final long deviceId, final long userId) {
        final List<Long> userIds = watchersOfDevice.get(deviceId);
        final List<Long> deviceIds = watchedDevicesOfUser.get(userId);
        if (userIds.remove(userId)) {
            watchersOfDevice.put(deviceId, userIds);
        }
        if (deviceIds.remove(deviceId)) {
            watchedDevicesOfUser.put(userId, deviceIds);
        }
    }
}
