package com.zeyad.usecases.data.mappers;

/**
 * @author zeyad on 12/13/16.
 */
public class DefaultDAOMapper extends DAOMapper {
    private static DefaultDAOMapper sDefaultDAOMapper;
    public DefaultDAOMapper() {
        super();
    }

    public static DefaultDAOMapper getInstance() {
        if (sDefaultDAOMapper == null) {
            sDefaultDAOMapper = new DefaultDAOMapper();
        }
        return sDefaultDAOMapper;
    }

    @Override
    public Object mapToDomainManual(Object object) {
        return object;
    }
}
