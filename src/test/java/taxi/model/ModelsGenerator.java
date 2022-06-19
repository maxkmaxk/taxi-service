package taxi.model;

class ModelsGenerator {

    public static Manufacturer generateManufacturer(String country, String name) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setCountry(country);
        manufacturer.setName(name);
        return manufacturer;
    }

    public static Manufacturer generatePersistentManufacturer(Long id, Manufacturer expected) {
        expected.setId(id);
        return expected;
    }

    public static Driver generateDriver(String login, String name, String password,
                                        String licenseNumber) {
        Driver driver = new Driver();
        driver.setLogin(login);
        driver.setName(name);
        driver.setPassword(password);
        driver.setLicenseNumber(licenseNumber);
        return driver;
    }

    public static Driver generatePersistentDriver (Long id, Driver driver) {
        driver.setId(id);
        return driver;
    }

    public static Car generateCar(String model, Manufacturer manufacturer) {
        return new Car(model, manufacturer);
    }

    public static Car generatePersistentCar(Long id, Car car) {
        car.setId(id);
        return car;
    }
}
