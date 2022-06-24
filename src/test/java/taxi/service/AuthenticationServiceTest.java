package taxi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;

import java.lang.reflect.Field;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import taxi.exception.AuthenticationException;
import taxi.exception.DataProcessingException;
import taxi.model.Driver;
import taxi.model.ModelsGenerator;

class AuthenticationServiceTest {
    private static final String DRIVER_1_LOGIN = "driver1@mail.com";
    private static final String DRIVER_1_PASSWORD = "driver 1 password";
    private static final String DRIVER_2_LOGIN = "driver2@mail.com";
    private static final String DRIVER_2_PASSWORD = "driver 2 password";
    private static final Long DRIVER_1_ID = 1l;
    private static final Long DRIVER_2_ID = 2l;
    private static Driver firstDriver;
    private static Driver secondDriver;
    private static DriverService driverService;
    private static AuthenticationService authenticationService;

    @BeforeAll
    static void beforeAll() throws NoSuchFieldException, IllegalAccessException {
        String driver1Name = "driver 1";
        String driver1Licensenumber = "driver 1 license";
        firstDriver = ModelsGenerator.generateDriver(DRIVER_1_LOGIN,
                driver1Name, DRIVER_1_PASSWORD, driver1Licensenumber);
        String driver2Name = "driver 2";
        String driver2Licensenumber = "driver 2 license";
        secondDriver = ModelsGenerator.generateDriver(DRIVER_2_LOGIN,
                driver2Name, DRIVER_2_PASSWORD, driver2Licensenumber);
        driverService = Mockito.mock(DriverService.class);
        authenticationService = new AuthenticationServiceImpl();
        injectDriverService();
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(driverService);
    }

    @Test
    void login_Ok() throws AuthenticationException {
        Mockito.when(driverService.findByLogin(DRIVER_1_LOGIN)).thenReturn(Optional.of(
                ModelsGenerator.generatePersistentDriver(DRIVER_1_ID, firstDriver)));
        Driver actual = authenticationService.login(DRIVER_1_LOGIN, DRIVER_1_PASSWORD);
        assertNotNull(actual);
        assertEquals(firstDriver, actual);
        Mockito.when(driverService.findByLogin(DRIVER_2_LOGIN)).thenReturn(Optional.of(
                ModelsGenerator.generatePersistentDriver(DRIVER_2_ID, secondDriver)));
        actual = authenticationService.login(DRIVER_2_LOGIN, DRIVER_2_PASSWORD);
        assertNotNull(actual);
        assertEquals(secondDriver, actual);
    }

    @Test
    void login_authException_notOk() {
        Mockito.when(driverService.findByLogin(DRIVER_1_LOGIN)).thenReturn(Optional.of(
                ModelsGenerator.generatePersistentDriver(DRIVER_1_ID, firstDriver)));
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

    private static void injectDriverService() throws NoSuchFieldException, IllegalAccessException {
        Field driverServiceField = AuthenticationServiceImpl.class.getDeclaredField("driverService");
        driverServiceField.setAccessible(true);
        driverServiceField.set(authenticationService, driverService);
    }

}