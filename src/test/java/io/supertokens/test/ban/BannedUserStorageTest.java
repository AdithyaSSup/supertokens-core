/*
 *    Copyright (c) 2023, VRAI Labs and/or its affiliates. All rights reserved.
 *
 *    This software is licensed under the Apache License, Version 2.0 (the
 *    "License") as published by the Apache Software Foundation.
 *
 *    You may not use this file except in compliance with the License. You may
 *    obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *    License for the specific language governing permissions and limitations
 *    under the License.
 */

package io.supertokens.test.ban;

import io.supertokens.ProcessState;
import io.supertokens.emailpassword.EmailPassword;
import io.supertokens.pluginInterface.STORAGE_TYPE;
import io.supertokens.pluginInterface.ban.BannedUserStorage;
import io.supertokens.pluginInterface.ban.exceptions.DuplicateUserIdException;
import io.supertokens.pluginInterface.ban.exceptions.UnknownUserIdException;
import io.supertokens.pluginInterface.ban.exceptions.UserNotBannedException;
import io.supertokens.pluginInterface.emailpassword.UserInfo;
import io.supertokens.pluginInterface.exceptions.StorageQueryException;
import io.supertokens.storageLayer.StorageLayer;
import io.supertokens.test.TestingProcessManager;
import io.supertokens.test.Utils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import static org.junit.Assert.*;

public class BannedUserStorageTest {
    @Rule
    public TestRule watchman = Utils.getOnFailure();

    @AfterClass
    public static void afterTesting() {
        Utils.afterTesting();
    }

    @Before
    public void beforeEach() {
        Utils.reset();
    }

    @Test
    public void testCreateNewBannedUser() throws Exception {

        String[] args = {"../"};

        TestingProcessManager.TestingProcess process = TestingProcessManager.start(args);
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STARTED));

        if (StorageLayer.getStorage(process.getProcess()).getType() != STORAGE_TYPE.SQL) {
            return;
        }

        BannedUserStorage bannedUserStorage = StorageLayer.getBannedUserStorage(process.getProcess());
        // create a user
        UserInfo userInfo = EmailPassword.signUp(process.main, "test1@example.com", "testPassword");


        bannedUserStorage.createNewBannedUser(userInfo.id);

        // get user ban status to verify
        assertTrue(bannedUserStorage.isUserBanned(userInfo.id));


        process.kill();
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STOPPED));
    }

    @Test
    public void testCreateNewNonExistingBannedUser() throws Exception {

        String[] args = {"../"};

        TestingProcessManager.TestingProcess process = TestingProcessManager.start(args);
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STARTED));

        if (StorageLayer.getStorage(process.getProcess()).getType() != STORAGE_TYPE.SQL) {
            return;
        }


        BannedUserStorage bannedUserStorage = StorageLayer.getBannedUserStorage(process.getProcess());
        // pass a random user Id

        String userId = "random";
        Exception error = null;
        try {
            bannedUserStorage.createNewBannedUser(userId);
        } catch (Exception e) {
            error = e;
        }

        assertNotNull(error);
        assertTrue(error instanceof UnknownUserIdException);


        process.kill();
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STOPPED));
    }

    @Test
    public void testCreateDuplicateBannedUser() throws Exception {

        String[] args = {"../"};

        TestingProcessManager.TestingProcess process = TestingProcessManager.start(args);
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STARTED));

        if (StorageLayer.getStorage(process.getProcess()).getType() != STORAGE_TYPE.SQL) {
            return;
        }


        BannedUserStorage bannedUserStorage = StorageLayer.getBannedUserStorage(process.getProcess());

        // create a user
        UserInfo userInfo = EmailPassword.signUp(process.main, "test@example.com", "testPassword");
        String userId = userInfo.id;

        bannedUserStorage.createNewBannedUser(userId);

        Exception error = null;
        try {
            bannedUserStorage.createNewBannedUser(userId);
        } catch (Exception e) {
            error = e;
        }

        assertNotNull(error);
        assertEquals(DuplicateUserIdException.class, error.getClass());
        assertTrue(error instanceof DuplicateUserIdException);


        process.kill();
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STOPPED));
    }

    @Test
    public void testCheckWhetherUserIsBanned() throws Exception {

        String[] args = {"../"};

        TestingProcessManager.TestingProcess process = TestingProcessManager.start(args);
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STARTED));

        if (StorageLayer.getStorage(process.getProcess()).getType() != STORAGE_TYPE.SQL) {
            return;
        }

        BannedUserStorage bannedUserStorage = StorageLayer.getBannedUserStorage(process.getProcess());
        // create a user
        UserInfo userInfo = EmailPassword.signUp(process.main, "test2@example.com", "testPassword");


        // check whether the newly created user is banned
        assertFalse(bannedUserStorage.isUserBanned(userInfo.id));


        process.kill();
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STOPPED));
    }
    @Test
    public void testDeleteBannedUser() throws Exception {
        String[] args = {"../"};

        TestingProcessManager.TestingProcess process = TestingProcessManager.start(args);
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STARTED));

        if (StorageLayer.getStorage(process.getProcess()).getType() != STORAGE_TYPE.SQL) {
            return;
        }


        BannedUserStorage bannedUserStorage = StorageLayer.getBannedUserStorage(process.getProcess());
        // create a user
        UserInfo userInfo = EmailPassword.signUp(process.main, "test3@example.com", "testPassword");


        bannedUserStorage.createNewBannedUser(userInfo.id);
        bannedUserStorage.removeBannedUser(userInfo.id);

        assertFalse(bannedUserStorage.isUserBanned(userInfo.id));


        process.kill();
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STOPPED));
    }

    @Test
    public void testDeleteUnBannedUser() throws Exception {
        String[] args = {"../"};

        TestingProcessManager.TestingProcess process = TestingProcessManager.start(args);
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STARTED));

        if (StorageLayer.getStorage(process.getProcess()).getType() != STORAGE_TYPE.SQL) {
            return;
        }


        BannedUserStorage bannedUserStorage = StorageLayer.getBannedUserStorage(process.getProcess());
        // create a user
        UserInfo userInfo = EmailPassword.signUp(process.main, "test4@example.com", "testPassword");


        Exception error = null;
        try {
            bannedUserStorage.removeBannedUser(userInfo.id);
        } catch (Exception e) {
            error = e;
        }

        assertNotNull(error);
        assertTrue(error instanceof UserNotBannedException);


        process.kill();
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STOPPED));
    }
}
