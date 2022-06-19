package taxi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import taxi.dao.ManufacturerDao;
import taxi.exception.DataProcessingException;
import taxi.model.Manufacturer;

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
    private static final String FIRST_COUNTRY = "USA";
    private static final String FIRST_NAME = "Ford";
    private static final Long FIRST_ID = 1l;
    private static final String SECOND_COUNTRY = "Japan";
    private static final String SECOND_NAME = "Toyota";
    private static final Long SECOND_ID = 2l;
    private static final Manufacturer FIRST_MANUFACTURER =
            mockManufacturer(FIRST_COUNTRY, FIRST_NAME);
    private static final Manufacturer SECOND_MANUFACTURER =
            mockManufacturer(SECOND_COUNTRY, SECOND_NAME);
    private ManufacturerService manufacturerService = new ManufacturerServiceImpl();
    private ManufacturerDao manufacturerDao = Mockito.mock(ManufacturerDao.class);

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        injectDao(manufacturerDao);
    }

    @Test
    void create_Ok() {
        Mockito.when(manufacturerDao.create(FIRST_MANUFACTURER))
                .thenReturn(mockPersistentManufacturer(FIRST_ID, FIRST_MANUFACTURER));
        Manufacturer actual = manufacturerService.create(FIRST_MANUFACTURER);
        assertNotNull(actual);
        assertEquals(FIRST_MANUFACTURER, actual);
    }

    @Test
    void create_dataProcException_notOk() {
        Mockito.when(manufacturerDao.create(any())).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class,
                () -> manufacturerService.create(FIRST_MANUFACTURER));
    }


    @Test
    void get_Ok() {
        Mockito.when(manufacturerDao.get(FIRST_ID))
                .thenReturn(Optional.of(mockPersistentManufacturer(FIRST_ID,
                        mockPersistentManufacturer(FIRST_ID, FIRST_MANUFACTURER))));
        Manufacturer actual = manufacturerService.get(FIRST_ID);
        assertNotNull(actual);
        assertEquals(FIRST_MANUFACTURER, actual);
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
                mockPersistentManufacturer(FIRST_ID, FIRST_MANUFACTURER),
                mockPersistentManufacturer(SECOND_ID, SECOND_MANUFACTURER));
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
        Mockito.when(manufacturerDao.update(FIRST_MANUFACTURER))
                .thenReturn(mockPersistentManufacturer(FIRST_ID, FIRST_MANUFACTURER));
        Manufacturer actual = manufacturerService.update(FIRST_MANUFACTURER);
        assertNotNull(actual);
        assertEquals(FIRST_MANUFACTURER, actual);
    }

    @Test
    void update_dataProcException_notOk() {
        Mockito.when(manufacturerDao.update(any())).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class,
                () -> manufacturerService.update(
                        mockPersistentManufacturer(FIRST_ID, FIRST_MANUFACTURER)));
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

    private static Manufacturer mockManufacturer(String country, String name) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setCountry(country);
        manufacturer.setName(name);
        return manufacturer;
    }

    private static Manufacturer mockPersistentManufacturer(Long id, Manufacturer expected) {
        expected.setId(id);
        return expected;
    }

    private static Manufacturer mockPersistentManufacturer(Long id, String country, String name) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(id);
        manufacturer.setCountry(country);
        manufacturer.setName(name);
        return manufacturer;
    }

    private void injectDao(ManufacturerDao manufacturerDao)
            throws NoSuchFieldException, IllegalAccessException {
        Field manufacturerDaoField =
                ManufacturerServiceImpl.class.getDeclaredField("manufacturerDao");
        manufacturerDaoField.setAccessible(true);
        manufacturerDaoField.set(manufacturerService, manufacturerDao);
    }

}
