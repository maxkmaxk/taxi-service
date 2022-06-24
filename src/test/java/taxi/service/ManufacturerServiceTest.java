package taxi.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import taxi.dao.ManufacturerDao;
import taxi.exception.DataProcessingException;
import taxi.model.Manufacturer;
import taxi.model.ModelsGenerator;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

class ManufacturerServiceTest {
    private static final Long FIRST_ID = 1l;
    private static final Long SECOND_ID = 1l;
    private static Manufacturer firstManufacturer;
    private static Manufacturer secondManufacturer;
    private static ManufacturerService manufacturerService;
    private static ManufacturerDao manufacturerDao;

    @BeforeAll
    static void beforeAll() throws NoSuchFieldException, IllegalAccessException {
        String firstCountry = "USA";
        String firstName = "Ford";
        firstManufacturer =
                ModelsGenerator.generateManufacturer(firstCountry, firstName);
        String secondCountry = "Japan";
        String secondName = "Toyota";
        secondManufacturer =
                ModelsGenerator.generateManufacturer(secondCountry, secondName);
        manufacturerService =  new ManufacturerServiceImpl();
        manufacturerDao = Mockito.mock(ManufacturerDao.class);
        injectDao(manufacturerDao);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(manufacturerDao);
    }

    @Test
    void create_Ok() {
        Mockito.when(manufacturerDao.create(firstManufacturer))
                .thenReturn(ModelsGenerator.generatePersistentManufacturer(FIRST_ID,
                        firstManufacturer));
        Manufacturer actual = manufacturerService.create(firstManufacturer);
        assertNotNull(actual);
        assertEquals(firstManufacturer, actual);
    }

    @Test
    void create_dataProcException_notOk() {
        Mockito.when(manufacturerDao.create(any())).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class,
                () -> manufacturerService.create(firstManufacturer));
    }


    @Test
    void get_Ok() {
        Mockito.when(manufacturerDao.get(FIRST_ID))
                .thenReturn(Optional.of(ModelsGenerator.generatePersistentManufacturer(FIRST_ID,
                        firstManufacturer)));
        Manufacturer actual = manufacturerService.get(FIRST_ID);
        assertNotNull(actual);
        assertEquals(firstManufacturer, actual);
    }

    @Test
    void get_noSuchElement_notOk() {
        Mockito.when(manufacturerDao.get(FIRST_ID))
                .thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> manufacturerService.get(FIRST_ID));
    }

    @Test
    void get_dataProcException_notOk() {
        Mockito.when(manufacturerDao.get(any())).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class, () -> manufacturerService.get(FIRST_ID));
    }

    @Test
    void getAll_Ok() {
        List<Manufacturer> expected = List.of(
                ModelsGenerator.generatePersistentManufacturer(FIRST_ID, firstManufacturer),
                ModelsGenerator.generatePersistentManufacturer(SECOND_ID, secondManufacturer));
        Mockito.when(manufacturerDao.getAll()).thenReturn(expected);
        List<Manufacturer> actual = manufacturerService.getAll();
        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertTrue(expected.containsAll(actual));
    }

    @Test
    void getAll_Empty_Ok() {
        Mockito.when(manufacturerDao.getAll()).thenReturn(Collections.emptyList());
        List<Manufacturer> actual = manufacturerService.getAll();
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void getAll_dataProcException_notOk() {
        Mockito.when(manufacturerDao.getAll()).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class, () -> manufacturerService.getAll());
    }

    @Test
    void update_Ok() {
        Mockito.when(manufacturerDao.update(firstManufacturer))
                .thenReturn(ModelsGenerator.generatePersistentManufacturer(FIRST_ID,
                        firstManufacturer));
        Manufacturer actual = manufacturerService.update(firstManufacturer);
        assertNotNull(actual);
        assertEquals(firstManufacturer, actual);
    }

    @Test
    void update_dataProcException_notOk() {
        Mockito.when(manufacturerDao.update(any())).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class,
                () -> manufacturerService.update(
                        ModelsGenerator.generatePersistentManufacturer(FIRST_ID,
                                firstManufacturer)));
    }

    @Test
    void delete_Ok() {
        Mockito.when(manufacturerDao.delete(FIRST_ID)).thenReturn(true);
        assertTrue(manufacturerService.delete(FIRST_ID));
    }

    @Test
    void delete_notOk() {
        Mockito.when(manufacturerDao.delete(FIRST_ID)).thenReturn(false);
        assertFalse(manufacturerService.delete(FIRST_ID));
    }

    @Test
    void delete_dataProcException_notOk() {
        Mockito.when(manufacturerDao.delete(any())).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class, () -> manufacturerService.delete(FIRST_ID));
    }

    private static void injectDao(ManufacturerDao manufacturerDao)
            throws NoSuchFieldException, IllegalAccessException {
        Field manufacturerDaoField =
                ManufacturerServiceImpl.class.getDeclaredField("manufacturerDao");
        manufacturerDaoField.setAccessible(true);
        manufacturerDaoField.set(manufacturerService, manufacturerDao);
    }
}
