package com.itau.desafio.vendas.infrastructure.adapters.in.web.mapper;

import com.itau.desafio.vendas.domain.model.Vehicle;
import com.itau.desafio.vendas.infrastructure.adapters.in.web.dto.VehicleRequest;

public class VehicleMapper {

    public static Vehicle toDomain(VehicleRequest dto) {
        if (dto == null) {
            return null;
        }
        return Vehicle.create(
                dto.model(),
                dto.cost(),
                dto.manufactureYear(),
                "web-request"
        );
    }
}