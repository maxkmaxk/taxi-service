package taxi.service;

import org.junit.jupiter.api.BeforeAll;
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

    private static final Long CAR_1_ID = 1l;
    private static final Long CAR_2_ID = 2l;
    private static Driver firstDriver;
    private static Driver secondDriver;
    private static Car firstCar;
    private static Car secondCar;
    private static CarService carService = new CarServiceImpl();
    private static CarDao carDao = Mockito.mock(CarDao.class);

    @BeforeAll
    static void beforeAll() throws NoSuchFieldException, IllegalAccessException {
        String manufacturer1Country = "Manufacturer 1 country";
        String manufacturer1Name = "Manufacturer 1 name";
        Manufacturer firstManufacturer = ModelsGenerator.generateManufacturer(
                manufacturer1Country, manufacturer1Name);
        String manufacturer2Country = "Manufacturer 2 country";
        String manufacturer2Name = "Manufacturer 2 name";
        Manufacturer secondManufacturer = ModelsGenerator.generateManufacturer(
                manufacturer2Country, manufacturer2Name);

        String driver1Name = "driver 1";
        String driver1Login = "driver1@mail.com";
        String driver1Password = "driver 1 password";
        String driver1Licensenumber = "driver 1 password";
        firstDriver = ModelsGenerator.generateDriver(driver1Login,
                driver1Name, driver1Password, driver1Licensenumber);
        String driver2Name = "driver 2";
        String driver2Login = "driver2@mail.com";
        String driver2Password = "driver 2 password";
        String driver2Licensenumber = "driver 2 password";
        secondDriver = ModelsGenerator.generateDriver(driver2Login,
                driver2Name, driver2Password, driver2Licensenumber);

        String car1Model = "Car 1";
        firstCar = ModelsGenerator.generateCar(car1Model, firstManufacturer);
        String car2Model = "Car 2";
        secondCar = ModelsGenerator.generateCar(car2Model, secondManufacturer);
        carService = new CarServiceImpl();
        carDao = Mockito.mock(CarDao.class);
        injectCarDao();
    }

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        firstCar.getDrivers().clear();
        secondCar.getDrivers().clear();
        Mockito.reset(carDao);
    }

    @Test
    void addDriverToCar_Ok() {
        Mockito.when(carDao.update(any())).thenAnswer(i -> i.getArgument(0));
        carService.addDriverToCar(firstDriver, firstCar);
        assertEquals(1, firstCar.getDrivers().size());
        assertEquals(firstDriver, firstCar.getDrivers().get(0));
        carService.addDriverToCar(secondDriver, firstCar);
        assertEquals(2, firstCar.getDrivers().size());
        assertEquals(secondDriver, firstCar.getDrivers().get(1));
    }

    @Test
    void addDriverToCar_dataProcException_notOk() {
        Mockito.when(carDao.update(any())).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class,
                () -> carService.addDriverToCar(firstDriver, firstCar));
    }

    @Test
    void removeDriverFromCar_Ok() {
        firstCar.getDrivers().add(firstDriver);
        firstCar.getDrivers().add(secondDriver);
        Mockito.when(carDao.update(any())).thenAnswer(i -> i.getArgument(0));
        carService.removeDriverFromCar(firstDriver, firstCar);
        assertEquals(1, firstCar.getDrivers().size());
        assertFalse(firstCar.getDrivers().contains(firstDriver));
        assertTrue(firstCar.getDrivers().contains(secondDriver));
        carService.removeDriverFromCar(secondDriver, firstCar);
        assertTrue(firstCar.getDrivers().isEmpty());
    }

    @Test
    void getAllByDriver_Ok() {
        firstCar.getDrivers().add(firstDriver);
        List<Car> expected = new ArrayList<>();
        expected.add(firstCar);
        Mockito.when(carDao.getAllByDriver(firstDriver.getId())).thenReturn(expected);
        List<Car> actual = carService.getAllByDriver(firstDriver.getId());
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
        secondCar.getDrivers().add(secondDriver);
        expected.add(secondCar);
        Mockito.when(carDao.getAllByDriver(firstDriver.getId())).thenReturn(expected);
        actual = carService.getAllByDriver(firstDriver.getId());
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    void getAllByDriver_dataProcException_notOk() {
        Mockito.when(carDao.getAllByDriver(any())).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class,
                () -> carService.getAllByDriver(firstDriver.getId()));
    }

    @Test
    void create_Ok() {
        Mockito.when(carDao.create(firstCar))
                .thenReturn(ModelsGenerator.generatePersistentCar(CAR_1_ID, firstCar));
        Car actual = carService.create(firstCar);
        assertNotNull(actual);
        assertEquals(firstCar, actual);
        Mockito.when(carDao.create(secondCar))
                .thenReturn(ModelsGenerator.generatePersistentCar(CAR_2_ID, secondCar));
        actual = carService.create(secondCar);
        assertNotNull(actual);
        assertEquals(secondCar, actual);
    }

    @Test
    void create_dataProcException_notOk() {
        Mockito.when(carDao.create(any())).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class,
                () -> carService.create(firstCar));
    }

    @Test
    void get_Ok() {
        Mockito.when(carDao.get(CAR_1_ID)).thenReturn(Optional.of(
                ModelsGenerator.generatePersistentCar(CAR_1_ID, firstCar)));
        Car actual = carService.get(CAR_1_ID);
        assertNotNull(actual);
        assertEquals(firstCar, actual);
        Mockito.when(carDao.get(CAR_2_ID)).thenReturn(Optional.of(
                ModelsGenerator.generatePersistentCar(CAR_2_ID, secondCar)));
        actual = carService.get(CAR_2_ID);
        assertNotNull(actual);
        assertEquals(secondCar, actual);
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
                ModelsGenerator.generatePersistentCar(CAR_1_ID, firstCar)));
        List<Car> actual = carService.getAll();
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertTrue(actual.contains(firstCar));
        Mockito.when(carDao.getAll()).thenReturn(List.of(
                ModelsGenerator.generatePersistentCar(CAR_1_ID, firstCar),
                ModelsGenerator.generatePersistentCar(CAR_2_ID, secondCar)));
        actual = carService.getAll();
        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertTrue(actual.containsAll(List.of(firstCar, secondCar)));
    }

    @Test
    void update_Ok() {
        Mockito.when(carDao.update(any())).thenAnswer(i -> i.getArgument(0));
        Car actual = carService.update(ModelsGenerator.generatePersistentCar(CAR_1_ID, firstCar));
        assertNotNull(actual);
        assertEquals(firstCar, actual);
        actual = carService.update(ModelsGenerator.generatePersistentCar(CAR_2_ID, secondCar));
        assertNotNull(actual);
        assertEquals(secondCar, actual);
    }

    @Test
    void update_dataProcException_notOk() {
        Mockito.when(carDao.update(any())).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class,
                () -> carService.update(firstCar));
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

    private static void injectCarDao() throws NoSuchFieldException, IllegalAccessException {
        Field carDaoField = CarServiceImpl.class.getDeclaredField("carDao");
        carDaoField.setAccessible(true);
        carDaoField.set(carService, carDao);
    }
}