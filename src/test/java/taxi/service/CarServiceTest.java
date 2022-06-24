package taxi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import taxi.dao.CarDao;
import taxi.exception.DataProcessingException;
import taxi.model.Car;
import taxi.model.Driver;
import taxi.model.Manufacturer;
import taxi.model.ModelsGenerator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

class CarServiceTest {
    private static final String MANUFACTURER_1_COUNTRY = "Manufacturer 1 country";
    private static final String MANUFACTURER_1_NAME = "Manufacturer 1 name";
    private static final Long MANUFACTURER_1_ID = 1l;
    private static final Manufacturer MANUFACTURER_1 = ModelsGenerator.generateManufacturer(
            MANUFACTURER_1_COUNTRY, MANUFACTURER_1_NAME);
    private static final String MANUFACTURER_2_COUNTRY = "Manufacturer 2 country";
    private static final String MANUFACTURER_2_NAME = "Manufacturer 2 name";
    private static final Long MANUFACTURER_2_ID = 2l;
    private static final Manufacturer MANUFACTURER_2 = ModelsGenerator.generateManufacturer(
            MANUFACTURER_2_COUNTRY, MANUFACTURER_2_NAME);

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

    private static final String CAR_1_MODEL = "Car 1";
    private static final Long CAR_1_ID = 1l;
    private static final Car CAR_1 = ModelsGenerator.generateCar(CAR_1_MODEL, MANUFACTURER_1);
    private static final String CAR_2_MODEL = "Car 1";
    private static final Long CAR_2_ID = 1l;
    private static final Car CAR_2 = ModelsGenerator.generateCar(CAR_2_MODEL, MANUFACTURER_2);

    private CarService carService = new CarServiceImpl();
    private CarDao carDao = Mockito.mock(CarDao.class);

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        injectCarDao();
        CAR_1.getDrivers().clear();
        CAR_2.getDrivers().clear();
    }

    @Test
    void addDriverToCar_Ok() {
        Mockito.when(carDao.update(any())).thenAnswer(i -> i.getArgument(0));
        carService.addDriverToCar(DRIVER_1, CAR_1);
        assertEquals(1, CAR_1.getDrivers().size());
        assertEquals(DRIVER_1, CAR_1.getDrivers().get(0));
        carService.addDriverToCar(DRIVER_2, CAR_1);
        assertEquals(2, CAR_1.getDrivers().size());
        assertEquals(DRIVER_2, CAR_1.getDrivers().get(1));
    }

    @Test
    void addDriverToCar_dataProcException_notOk() {
        Mockito.when(carDao.update(any())).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class,
                () -> carService.addDriverToCar(DRIVER_1, CAR_1));
    }

    @Test
    void removeDriverFromCar_Ok() {
        CAR_1.getDrivers().add(DRIVER_1);
        CAR_1.getDrivers().add(DRIVER_2);
        Mockito.when(carDao.update(any())).thenAnswer(i -> i.getArgument(0));
        carService.removeDriverFromCar(DRIVER_1, CAR_1);
        assertEquals(1, CAR_1.getDrivers().size());
        assertFalse(CAR_1.getDrivers().contains(DRIVER_1));
        assertTrue(CAR_1.getDrivers().contains(DRIVER_2));
        carService.removeDriverFromCar(DRIVER_2, CAR_1);
        assertTrue(CAR_1.getDrivers().isEmpty());
    }

    @Test
    void getAllByDriver_Ok() {
        CAR_1.getDrivers().add(DRIVER_1);
        List<Car> expected = new ArrayList<>();
        expected.add(CAR_1);
        Mockito.when(carDao.getAllByDriver(DRIVER_1.getId())).thenReturn(expected);
        List<Car> actual = carService.getAllByDriver(DRIVER_1.getId());
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
        CAR_2.getDrivers().add(DRIVER_2);
        expected.add(CAR_2);
        Mockito.when(carDao.getAllByDriver(DRIVER_1.getId())).thenReturn(expected);
        actual = carService.getAllByDriver(DRIVER_1.getId());
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    void getAllByDriver_dataProcException_notOk() {
        Mockito.when(carDao.getAllByDriver(any())).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class,
                () -> carService.getAllByDriver(DRIVER_1.getId()));
    }

    @Test
    void create_Ok() {
        Mockito.when(carDao.create(CAR_1))
                .thenReturn(ModelsGenerator.generatePersistentCar(CAR_1_ID, CAR_1));
        Car actual = carService.create(CAR_1);
        assertNotNull(actual);
        assertEquals(CAR_1, actual);
        Mockito.when(carDao.create(CAR_2))
                .thenReturn(ModelsGenerator.generatePersistentCar(CAR_2_ID, CAR_2));
        actual = carService.create(CAR_2);
        assertNotNull(actual);
        assertEquals(CAR_2, actual);
    }

    @Test
    void create_dataProcException_notOk() {
        Mockito.when(carDao.create(any())).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class,
                () -> carService.create(CAR_1));
    }

    @Test
    void get_Ok() {
        Mockito.when(carDao.get(CAR_1_ID)).thenReturn(Optional.of(
                ModelsGenerator.generatePersistentCar(CAR_1_ID, CAR_1)));
        Car actual = carService.get(CAR_1_ID);
        assertNotNull(actual);
        assertEquals(CAR_1, actual);
        Mockito.when(carDao.get(CAR_2_ID)).thenReturn(Optional.of(
                ModelsGenerator.generatePersistentCar(CAR_2_ID, CAR_2)));
        actual = carService.get(CAR_2_ID);
        assertNotNull(actual);
        assertEquals(CAR_2, actual);
    }

    @Test
    void get_noSuchElement_notOk() {
        Mockito.when(carDao.get(any())).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> carService.get(CAR_1_ID));
        assertThrows(NoSuchElementException.class, () -> carService.get(CAR_2_ID));
    }

    @Test
    void get_dataProcException_notOk() {
        Mockito.when(carDao.get(any())).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class,
                () -> carService.get(CAR_1_ID));
    }

    @Test
    void getAll_Ok() {
        Mockito.when(carDao.getAll()).thenReturn(List.of(
                ModelsGenerator.generatePersistentCar(CAR_1_ID, CAR_1)));
        List<Car> actual = carService.getAll();
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertTrue(actual.contains(CAR_1));
        Mockito.when(carDao.getAll()).thenReturn(List.of(
                ModelsGenerator.generatePersistentCar(CAR_1_ID, CAR_1),
                ModelsGenerator.generatePersistentCar(CAR_2_ID, CAR_2)));
        actual = carService.getAll();
        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertTrue(actual.containsAll(List.of(CAR_1, CAR_2)));
    }

    @Test
    void update_Ok() {
        Mockito.when(carDao.update(any())).thenAnswer(i -> i.getArgument(0));
        Car actual = carService.update(ModelsGenerator.generatePersistentCar(CAR_1_ID, CAR_1));
        assertNotNull(actual);
        assertEquals(CAR_1, actual);
        actual = carService.update(ModelsGenerator.generatePersistentCar(CAR_2_ID, CAR_2));
        assertNotNull(actual);
        assertEquals(CAR_2, actual);
    }

    @Test
    void update_dataProcException_notOk() {
        Mockito.when(carDao.update(any())).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class,
                () -> carService.update(CAR_1));
    }

    @Test
    void delete_Ok() {
        Mockito.when(carDao.delete(any())).thenReturn(true);
        assertTrue(carService.delete(CAR_1_ID));
        assertTrue(carService.delete(CAR_2_ID));
    }

    @Test
    void delete_notOk() {
        Mockito.when(carDao.delete(any())).thenReturn(false);
        assertFalse(carService.delete(CAR_1_ID));
        assertFalse(carService.delete(CAR_2_ID));
    }

    @Test
    void delete_dataProcException_notOk() {
        Mockito.when(carDao.delete(any())).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class,
                () -> carService.delete(CAR_1_ID));
        assertThrows(DataProcessingException.class,
                () -> carService.delete(CAR_2_ID));
    }

    private void injectCarDao() throws NoSuchFieldException, IllegalAccessException {
        Field carDaoField = CarServiceImpl.class.getDeclaredField("carDao");
        carDaoField.setAccessible(true);
        carDaoField.set(carService, carDao);
    }
}