/**
 * The MIT License
 * Copyright (c) 2018 Estonian Information System Authority (RIA),
 * Nordic Institute for Interoperability Solutions (NIIS), Population Register Centre (VRK)
 * Copyright (c) 2015-2017 Estonian Information System Authority (RIA), Population Register Centre (VRK)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.niis.xroad.restapi.repository;

import ee.ria.xroad.common.conf.serverconf.dao.LocalGroupDAOImpl;
import ee.ria.xroad.common.conf.serverconf.model.GroupMemberType;
import ee.ria.xroad.common.conf.serverconf.model.LocalGroupType;
import ee.ria.xroad.common.identifier.ClientId;

import lombok.extern.slf4j.Slf4j;
import org.niis.xroad.restapi.util.PersistenceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * groups repository
 */
@Slf4j
@Repository
@Transactional
public class GroupsRepository {

    private final PersistenceUtils persistenceUtils;

    @Autowired
    public GroupsRepository(PersistenceUtils persistenceUtils) {
        this.persistenceUtils = persistenceUtils;
    }

    public LocalGroupType getLocalGroupType(String groupCode, ClientId clientId) {
        LocalGroupDAOImpl localGroupDAO = new LocalGroupDAOImpl();
        return localGroupDAO.findLocalGroup(persistenceUtils.getCurrentSession(), groupCode, clientId);
    }

    /**
     * Executes a Hibernate saveOrUpdate(client)
     * @param localGroupType
     */
    public void saveOrUpdate(LocalGroupType localGroupType) {
        persistenceUtils.getCurrentSession().saveOrUpdate(localGroupType);
    }

    /**
     * Executes a Hibernate saveOrUpdate(client)
     * @param groupMemberType
     * @return
     */
    public void saveOrUpdate(GroupMemberType groupMemberType) {
        persistenceUtils.getCurrentSession().saveOrUpdate(groupMemberType);
    }
}
