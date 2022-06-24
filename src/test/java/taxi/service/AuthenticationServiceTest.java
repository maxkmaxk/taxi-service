package taxi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;

import java.lang.reflect.Field;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import taxi.exception.AuthenticationException;
import taxi.exception.DataProcessingException;
import taxi.model.Driver;
import taxi.model.ModelsGenerator;

class AuthenticationServiceTest {
    private static final String DRIVER_1_NAME = "driver 1";
    private static final String DRIVER_1_LOGIN = "driver1@mail.com";
    private static final String DRIVER_1_PASSWORD = "driver 1 password";
    private static final String DRIVER_1_LICENSENUMBER = "driver 1 password";
    private static final Long DRIVER_1_ID = 1l;
    private static final Driver DRIVER_1 = ModelsGenerator.generateDriver(DRIVER_1_LOGIN,
            DRIVER_1_NAME, DRIVER_1_PASSWORD, DRIVER_1_LICENSENUMBER);
    private static final String DRIVER_2_NAME = "driver 2";
    private static final String DRIVER_2_LOGIN = "driver2@mail.com";
    private static final String DRIVER_2_PASSWORD = "driver 2 password";
    private static final String DRIVER_2_LICENSENUMBER = "driver 2 password";
    private static final Long DRIVER_2_ID = 2l;
    private static final Driver DRIVER_2 = ModelsGenerator.generateDriver(DRIVER_2_LOGIN,
            DRIVER_2_NAME, DRIVER_2_PASSWORD, DRIVER_2_LICENSENUMBER);
    private DriverService driverService = Mockito.mock(DriverService.class);
    private AuthenticationService authenticationService = new AuthenticationServiceImpl();

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        injectDriverService();
    }

    @Test
    void login_Ok() throws AuthenticationException {
        Mockito.when(driverService.findByLogin(DRIVER_1_LOGIN)).thenReturn(Optional.of(
                ModelsGenerator.generatePersistentDriver(DRIVER_1_ID, DRIVER_1)));
        Driver actual = authenticationService.login(DRIVER_1_LOGIN, DRIVER_1_PASSWORD);
        assertNotNull(actual);
        assertEquals(DRIVER_1, actual);
        Mockito.when(driverService.findByLogin(DRIVER_2_LOGIN)).thenReturn(Optional.of(
                ModelsGenerator.generatePersistentDriver(DRIVER_2_ID, DRIVER_2)));
        actual = authenticationService.login(DRIVER_2_LOGIN, DRIVER_2_PASSWORD);
        assertNotNull(actual);
        assertEquals(DRIVER_2, actual);
    }

    @Test
    void login_authException_notOk() {
        Mockito.when(driverService.findByLogin(DRIVER_1_LOGIN)).thenReturn(Optional.of(
                ModelsGenerator.generatePersistentDriver(DRIVER_1_ID, DRIVER_1)));
        assertThrows(AuthenticationException.class,
                () -> authenticationService.login(DRIVER_1_LOGIN, DRIVER_2_PASSWORD),
                "incorrect password");
        Mockito.when(driverService.findByLogin(DRIVER_2_LOGIN)).thenReturn(Optional.empty());
        assertThrows(AuthenticationException.class,
                () -> authenticationService.login(DRIVER_2_LOGIN, DRIVER_2_PASSWORD),
                "user not found");
        Mockito.when(driverService.findByLogin(DRIVER_2_LOGIN)).thenReturn(Optional.empty());
    }

    @Test
    void login_dataProcException_notOk() throws AuthenticationException {
        Mockito.when(driverService.findByLogin(anyString()))
                .thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class,
                () -> authenticationService.login(DRIVER_1_LOGIN, DRIVER_1_PASSWORD));
        assertThrows(DataProcessingException.class,
                () -> authenticationService.login(DRIVER_2_LOGIN, DRIVER_2_PASSWORD));
    }

    private void injectDriverService() throws NoSuchFieldException, IllegalAccessException {
        Field driverServiceField = AuthenticationServiceImpl.class.getDeclaredField("driverService");
        driverServiceField.setAccessible(true);
        driverServiceField.set(authenticationService, driverService);
    }

}