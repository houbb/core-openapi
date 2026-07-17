package io.coreplatform.openapi.application.port;

import io.coreplatform.openapi.application.domain.Parameter;

import java.util.List;
import java.util.Optional;

public interface ParameterRepository {

    Parameter save(Parameter parameter);

    Optional<Parameter> findById(Long id);

    List<Parameter> findByApiId(Long apiId);

    void deleteById(Long id);

    void deleteByApiId(Long apiId);
}
