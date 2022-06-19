package taxi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import taxi.dao.DriverDao;
import taxi.exception.DataProcessingException;
import taxi.model.Driver;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class DriverServiceTest {
    private static final String DRIVER_1_NAME = "driver 1";
    private static final String DRIVER_1_LOGIN = "driver1@mail.com";
    private static final String DRIVER_1_PASSWORD = "driver 1 password";
    private static final String DRIVER_1_LICENSENUMBER = "driver 1 password";
    private static final Long DRIVER_1_ID = 1l;
    private static final Driver DRIVER_1 = mockDriver(DRIVER_1_LOGIN, DRIVER_1_NAME,
            DRIVER_1_PASSWORD, DRIVER_1_LICENSENUMBER);
    private static final String DRIVER_2_NAME = "driver 2";
    private static final String DRIVER_2_LOGIN = "driver2@mail.com";
    private static final String DRIVER_2_PASSWORD = "driver 2 password";
    private static final String DRIVER_2_LICENSENUMBER = "driver 2 password";
    private static final Long DRIVER_2_ID = 2l;
    private static final Driver DRIVER_2 = mockDriver(DRIVER_2_LOGIN, DRIVER_2_NAME,
            DRIVER_2_PASSWORD, DRIVER_2_LICENSENUMBER);
    private final DriverService driverService = new DriverServiceImpl();
    private final DriverDao driverDao = Mockito.mock(DriverDao.class);

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        injectDriverDao();
    }

    @Test
    void create_Ok() {
        Mockito.when(driverDao.create(DRIVER_1)).thenReturn(mockPersistentDriver(DRIVER_1_ID,
                DRIVER_1));
        Mockito.when(driverDao.create(DRIVER_2)).thenReturn(mockPersistentDriver(DRIVER_2_ID,
                DRIVER_2));
        Driver actual = driverService.create(DRIVER_1);
        assertNotNull(actual);
        assertEquals(DRIVER_1, actual);
        actual = driverService.create(DRIVER_2);
        assertNotNull(actual);
        assertEquals(DRIVER_2, actual);
    }

    @Test
    void get_Ok() {
        Mockito.when(driverDao.get(DRIVER_1_ID))
                .thenReturn(Optional.of(mockPersistentDriver(DRIVER_1_ID, DRIVER_1)));
        Mockito.when(driverDao.get(DRIVER_2_ID))
                .thenReturn(Optional.of(mockPersistentDriver(DRIVER_2_ID, DRIVER_2)));
        Driver actual = driverService.get(DRIVER_1_ID);
        assertNotNull(actual);
        assertEquals(DRIVER_1, actual);
        actual = driverService.get(DRIVER_2_ID);
        assertNotNull(actual);
        assertEquals(DRIVER_2, actual);
    }

    @Test
    void get_noSuchElement_notOk() {
        Mockito.when(driverDao.get(DRIVER_1_ID))
                .thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> driverService.get(DRIVER_1_ID));
    }

    @Test
    void get_dataProcException_notOk() {
        Mockito.when(driverDao.get(DRIVER_1_ID)).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class, () -> driverService.get(DRIVER_1_ID));
    }

    @Test
    void getAll_Ok() {
        List<Driver> expected = List.of(mockPersistentDriver(DRIVER_1_ID, DRIVER_1),
                mockPersistentDriver(DRIVER_2_ID, DRIVER_2));
        Mockito.when(driverDao.getAll()).thenReturn(expected);
        List<Driver> actual = driverService.getAll();
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    void getAll_empty_Ok() {
        List<Driver> expected = Collections.emptyList();
        Mockito.when(driverDao.getAll()).thenReturn(expected);
        List<Driver> actual = driverService.getAll();
        assertTrue(actual.isEmpty());
    }

    @Test
    void getAll_dataProcException_notOk() {
        Mockito.when(driverDao.getAll()).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class, () -> driverService.getAll());
    }

    @Test
    void update_Ok() {
        Mockito.when(driverDao.update(any())).thenAnswer(i -> i.getArgument(0));
        Driver actual = driverService.update(mockPersistentDriver(DRIVER_1_ID, DRIVER_1));
        assertNotNull(actual);
        assertEquals(DRIVER_1, actual);
        actual = driverService.update(mockPersistentDriver(DRIVER_2_ID, DRIVER_2));
        assertNotNull(actual);
        assertEquals(DRIVER_2, actual);
    }

    @Test
    void update_dataProcException_notOk() {
        Mockito.when(driverDao.update(any())).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class,
                () -> driverService.update(mockPersistentDriver(DRIVER_1_ID, DRIVER_1)));
    }

    @Test
    void delete_exists_Ok() {
        Mockito.when(driverDao.delete(DRIVER_1_ID)).thenReturn(true);
        assertTrue(driverService.delete(DRIVER_1_ID));
    }

    @Test
    void delete_notExists_Ok() {
        Mockito.when(driverDao.delete(DRIVER_1_ID)).thenReturn(false);
        assertFalse(driverService.delete(DRIVER_1_ID));
    }

    @Test
    void delete_dataProcException_notOk() {
        Mockito.when(driverDao.delete(any())).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class,
                () -> driverService.delete(DRIVER_1_ID));
    }

    @Test
    void findByLogin_Ok() {
        Mockito.when(driverDao.findByLogin(DRIVER_1.getLogin()))
                .thenReturn(Optional.of(mockPersistentDriver(DRIVER_1_ID, DRIVER_1)));
        Mockito.when(driverDao.findByLogin(DRIVER_2.getLogin()))
                .thenReturn(Optional.of(mockPersistentDriver(DRIVER_2_ID, DRIVER_2)));
        Optional<Driver> actual = driverService.findByLogin(DRIVER_1.getLogin());
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertEquals(DRIVER_1, actual.get());
        actual = driverService.findByLogin(DRIVER_2.getLogin());
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertEquals(DRIVER_2, actual.get());
    }

    private void injectDriverDao() throws NoSuchFieldException, IllegalAccessException {
        Field driverDaoField = DriverServiceImpl.class.getDeclaredField("driverDao");
        driverDaoField.setAccessible(true);
        driverDaoField.set(driverService, driverDao);
    }

    private static Driver mockDriver(String login, String name, String password, String licenseNumber) {
        Driver driver = new Driver();
        driver.setLogin(login);
        driver.setName(name);
        driver.setPassword(password);
        driver.setLicenseNumber(licenseNumber);
        return driver;
    }

    private static Driver mockPersistentDriver (Long id, Driver driver) {
        driver.setId(id);
        return driver;
    }

}
