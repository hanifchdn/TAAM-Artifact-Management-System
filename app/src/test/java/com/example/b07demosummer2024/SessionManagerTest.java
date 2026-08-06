package com.example.b07demosummer2024;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

public class SessionManagerTest {

    private SessionManager sessionManager;
    private User user;

    // Setting up the test environment
    @Before
    public void setUp() {
        sessionManager = SessionManager.getInstance();
        sessionManager.clear();

        user = mock(User.class);
    }

    @Test
    public void getInstance_returnsSessionManager() { /* Tests if getInstance returns sessionManager
                                                         instance
                                                         */
        SessionManager result = SessionManager.getInstance();

        assertNotNull(result);
    }

    @Test
    public void getInstance_calledMultipleTimesReturnsSameInstance() { /* Tests if multiple calls of
                                                                          getInstance always returns
                                                                          the same instance
                                                                          */
        SessionManager firstInstance = SessionManager.getInstance();
        SessionManager secondInstance = SessionManager.getInstance();

        assertSame(firstInstance, secondInstance);
    }

    @Test
    public void getCurrentUser_noUserReturnsNull() { /* Tests if a clear session returns no current
                                                        user
                                                        */
        assertNull(sessionManager.getCurrentUser());
    }

    @Test
    public void setCurrentUser_validUserStoresUser() { /* Tests if setting the current user stores
                                                          the user in sessionManager
                                                          */
        sessionManager.setCurrentUser(user);

        assertSame(user, sessionManager.getCurrentUser());
    }

    @Test
    public void setCurrentUser_newUserReplacesExistingUser() { /* Tests if setting another user
                                                                  replaces previous user
                                                                  */
        User secondUser = mock(User.class);

        sessionManager.setCurrentUser(user);
        sessionManager.setCurrentUser(secondUser);

        assertSame(secondUser, sessionManager.getCurrentUser());
    }

    @Test
    public void clear_userIsStoredRemovesCurrentUser() { /* Tests if clearing the session clears
                                                            current user
                                                            */
        sessionManager.setCurrentUser(user);
        sessionManager.clear();

        assertNull(sessionManager.getCurrentUser());
    }

    @Test
    public void clear_noUserKeepsNullCurrentUser() { /* Tests if clearing the session with no
                                                        existing user keeps no existing user
                                                        */
        sessionManager.clear();

        assertNull(sessionManager.getCurrentUser());
    }
}