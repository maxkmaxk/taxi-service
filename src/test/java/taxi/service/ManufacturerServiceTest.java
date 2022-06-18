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
    private ManufacturerService manufacturerService = new ManufacturerServiceImpl();
    private ManufacturerDao manufacturerDao = Mockito.mock(ManufacturerDao.class);

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        injectDao(manufacturerDao);
    }

    @Test
    void create_Ok() {
        Manufacturer expected = new Manufacturer();
        expected.setCountry(FIRST_COUNTRY);
        expected.setName(FIRST_NAME);
        Mockito.when(manufacturerDao.create(expected)).thenReturn(mockPersistentManufacturer(FIRST_ID, expected));
        Manufacturer actual = manufacturerService.create(expected);
        assertNotNull(actual);
        assertEquals(expected.getCountry(), actual.getCountry());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(FIRST_ID, actual.getId());
    }

    @Test
    void create_exception_notOk() {
        Manufacturer expected = new Manufacturer();
        expected.setCountry(FIRST_COUNTRY);
        expected.setName(FIRST_NAME);
        Mockito.when(manufacturerDao.create(any())).thenThrow(DataProcessingException.class);
        assertThrows(DataProcessingException.class, () -> manufacturerService.create(expected));
    }


    @Test
    void get_Ok() {
        Manufacturer expected = new Manufacturer();
        expected.setCountry(FIRST_COUNTRY);
        expected.setName(FIRST_NAME);
        Mockito.when(manufacturerDao.get(FIRST_ID))
                .thenReturn(Optional.of(mockPersistentManufacturer(FIRST_ID, expected)));
        Manufacturer actual = manufacturerService.get(FIRST_ID);
        assertNotNull(actual);
        assertEquals(expected.getCountry(), actual.getCountry());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(FIRST_ID, actual.getId());
    }

    @Test
    void get_noSuchElement_notOk() {
        Mockito.when(manufacturerDao.get(FIRST_ID))
                .thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> manufacturerService.get(FIRST_ID));
    }

    @Test
    void getAll_Ok() {
        Mockito.when(manufacturerDao.getAll()).thenReturn(List.of(
                mockPersistentManufacturer(FIRST_ID, FIRST_COUNTRY, FIRST_NAME),
                mockPersistentManufacturer(SECOND_ID, SECOND_COUNTRY, SECOND_NAME)));
        List<Manufacturer> actual = manufacturerService.getAll();
        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals(FIRST_ID, actual.get(0).getId());
        assertEquals(FIRST_COUNTRY, actual.get(0).getCountry());
        assertEquals(FIRST_NAME, actual.get(0).getName());
        assertEquals(SECOND_ID, actual.get(1).getId());
        assertEquals(SECOND_COUNTRY, actual.get(1).getCountry());
        assertEquals(SECOND_NAME, actual.get(1).getName());
    }

    @Test
    void getAll_Empty_Ok() {
        Mockito.when(manufacturerDao.getAll()).thenReturn(Collections.emptyList());
        List<Manufacturer> actual = manufacturerService.getAll();
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void update_Ok() {
        Manufacturer expected = mockPersistentManufacturer(FIRST_ID, FIRST_COUNTRY, FIRST_NAME);
        Mockito.when(manufacturerDao.update(expected)).thenReturn(expected);
        Manufacturer actual = manufacturerService.update(expected);
        assertNotNull(actual);
        assertEquals(expected.getCountry(), actual.getCountry());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getId(), actual.getId());
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

    private Manufacturer mockPersistentManufacturer(Long id, Manufacturer expected) {
        expected.setId(id);
        return expected;
    }

    private Manufacturer mockPersistentManufacturer(Long id, String country, String name) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(id);
        manufacturer.setCountry(country);
        manufacturer.setName(name);
        return manufacturer;
    }

    private void injectDao(ManufacturerDao manufacturerDao) throws NoSuchFieldException, IllegalAccessException {
        Field manufacturerDaoField = ManufacturerServiceImpl.class.getDeclaredField(
                "manufacturerDao");
        manufacturerDaoField.setAccessible(true);
        manufacturerDaoField.set(manufacturerService, manufacturerDao);
    }

}
