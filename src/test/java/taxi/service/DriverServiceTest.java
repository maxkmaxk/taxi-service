package taxi.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import taxi.dao.DriverDao;
import taxi.exception.DataProcessingException;
import taxi.model.Driver;
import taxi.model.ModelsGenerator;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class DriverServiceTest {
    private static final Long DRIVER_1_ID = 1l;
    private static final Long DRIVER_2_ID = 2l;
    private static Driver firstDriver;
    private static Driver secondDriver;
    private static DriverService driverService;
    private static DriverDao driverDao;

    @BeforeAll
    static void beforeAll() throws NoSuchFieldException, IllegalAccessException {
        String driver1Name = "driver 1";
        String driver1Login = "driver1@mail.com";
        String driver1Password = "driver 1 password";
        String driver1Licensenumber = "driver 1 password";
        firstDriver = ModelsGenerator.generateDriver(driver1Login,
                driver1Name, driver1Password, driver1Licensenumber);
        String driver2Name = "driver 1";
        String driver2Login = "driver1@mail.com";
        String driver2Password = "driver 1 password";
        String driver2Licensenumber = "driver 1 password";
        secondDriver = ModelsGenerator.generateDriver(driver2Login,
                driver2Name, driver2Password, driver2Licensenumber);
        driverService = new DriverServiceImpl();
        driverDao = Mockito.mock(DriverDao.class);
        injectDriverDao();
    }

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        Mockito.reset(driverDao);
    }

    @Test
    void create_Ok() {
        Mockito.when(driverDao.create(firstDriver)).thenReturn(ModelsGenerator
                .generatePersistentDriver(DRIVER_1_ID, firstDriver));
        Mockito.when(driverDao.create(secondDriver)).thenReturn(ModelsGenerator
                .generatePersistentDriver(DRIVER_2_ID, secondDriver));
        Driver actual = driverService.create(firstDriver);
        assertNotNull(actual);
        assertEquals(firstDriver, actual);
        actual = driverService.create(secondDriver);
        assertNotNull(actual);
        assertEquals(secondDriver, actual);
    }

    @Test
    void get_Ok() {
        Mockito.when(driverDao.get(DRIVER_1_ID))
                .thenReturn(Optional.of(ModelsGenerator.generatePersistentDriver(DRIVER_1_ID,
                        firstDriver)));
        Mockito.when(driverDao.get(DRIVER_2_ID))
                .thenReturn(Optional.of(ModelsGenerator.generatePersistentDriver(DRIVER_2_ID,
                        secondDriver)));
        Driver actual = driverService.get(DRIVER_1_ID);
        assertNotNull(actual);
        assertEquals(firstDriver, actual);
        actual = driverService.get(DRIVER_2_ID);
        assertNotNull(actual);
        assertEquals(secondDriver, actual);
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
        List<Driver> expected = List.of(ModelsGenerator.generatePersistentDriver(DRIVER_1_ID,
                firstDriver),
                ModelsGenerator.generatePersistentDriver(DRIVER_2_ID, secondDriver));
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
        Driver actual = driverService.update(ModelsGenerator.generatePersistentDriver(DRIVER_1_ID,
                firstDriver));
        assertNotNull(actual);
        assertEquals(firstDriver, actual);
        actual = driverService.update(ModelsGenerator.generatePersistentDriver(DRIVER_2_ID,
                secondDriver));
        assertNotNull(actual);
        assertEquals(secondDriver, actual);
    }

    @Test
    void update_dataProcException_notOk() {
        Mockito.when(driverDao.update(any())).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class,
                () -> driverService.update(ModelsGenerator.generatePersistentDriver(DRIVER_1_ID,
                        firstDriver)));
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
        Mockito.when(driverDao.findByLogin(firstDriver.getLogin()))
                .thenReturn(Optional.of(ModelsGenerator.generatePersistentDriver(DRIVER_1_ID,
                        firstDriver)));
        Optional<Driver> actual = driverService.findByLogin(firstDriver.getLogin());
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertEquals(firstDriver, actual.get());
        Mockito.when(driverDao.findByLogin(secondDriver.getLogin()))
                .thenReturn(Optional.of(ModelsGenerator.generatePersistentDriver(DRIVER_2_ID,
                        secondDriver)));
        actual = driverService.findByLogin(secondDriver.getLogin());
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertEquals(secondDriver, actual.get());
    }

    private static void injectDriverDao() throws NoSuchFieldException, IllegalAccessException {
        Field driverDaoField = DriverServiceImpl.class.getDeclaredField("driverDao");
        driverDaoField.setAccessible(true);
        driverDaoField.set(driverService, driverDao);
    }

}
