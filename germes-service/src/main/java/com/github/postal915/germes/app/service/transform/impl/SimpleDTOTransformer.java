package com.github.postal915.germes.app.service.transform.impl;

import com.github.postal915.germes.app.infra.util.Checks;
import com.github.postal915.germes.app.infra.util.CommonUtil;
import com.github.postal915.germes.app.infra.util.ReflectionUtil;
import com.github.postal915.germes.app.model.entity.base.AbstractEntity;
import com.github.postal915.germes.app.rest.dto.base.BaseDTO;
import com.github.postal915.germes.app.service.transform.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default transformation engine that uses refection to transform objects
 */
public class SimpleDTOTransformer implements Transformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDTOTransformer.class);

    private final FieldProvider provider;

    public SimpleDTOTransformer() {
        provider = new CachedFieldProvider();
    }

    @Override
    public <T extends AbstractEntity, P extends BaseDTO<T>> P transform(
            final T entity, final Class<P> clz) {
        checkParams(entity, clz);

        P dto = ReflectionUtil.createInstance(clz);
        // Now just copy all the similar fields
        ReflectionUtil.copyFields(entity, dto, provider.getFieldNames(entity.getClass(), clz));
        dto.transform(entity);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SimpleDTOTransformer.transform: {} DTO object",
                    CommonUtil.toString(dto));
        }

        return dto;
    }

    private void checkParams(final Object param, final Class<?> clz) {

        Checks.checkParameter(param != null,
                "Source transformation object is not initialized");

        Checks.checkParameter(clz != null,
                "No class is defined for transformation");
    }

    @Override
    public <T extends AbstractEntity, P extends BaseDTO<T>> T unTransform(
            final P dto, final Class<T> clz) {
        checkParams(dto, clz);

        T entity = ReflectionUtil.createInstance(clz);

        ReflectionUtil.copyFields(dto, entity, provider.getFieldNames(dto.getClass(), clz));
        dto.unTransform(entity);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SimpleDTOTransformer.transform: {} entity",
                    CommonUtil.toString(dto));
        }

        return entity;
    }
}
