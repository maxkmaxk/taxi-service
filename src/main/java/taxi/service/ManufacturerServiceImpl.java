package taxi.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import taxi.dao.ManufacturerDao;
import taxi.lib.Inject;
import taxi.lib.Service;
import taxi.model.Manufacturer;

@Service
public class ManufacturerServiceImpl implements ManufacturerService {
    private static final Logger logger = LogManager.getLogger(ManufacturerServiceImpl.class);
    @Inject
    private ManufacturerDao manufacturerDao;

    @Override
    public Manufacturer create(Manufacturer manufacturer) {
        logger.info("create method was called. Params: manufacturer={}", manufacturer);
        return manufacturerDao.create(manufacturer);
    }

    @Override
    public Manufacturer get(Long id) {
        return manufacturerDao.get(id).get();
    }

    @Override
    public List<Manufacturer> getAll() {
        return manufacturerDao.getAll();
    }

    @Override
    public Manufacturer update(Manufacturer manufacturer) {
        logger.info("update method was called. Params: manufacturer={}", manufacturer);
        return manufacturerDao.update(manufacturer);
    }

    @Override
    public boolean delete(Long id) {
        logger.info("delete method was called. Params: id={}", id);
        return manufacturerDao.delete(id);
    }
}
