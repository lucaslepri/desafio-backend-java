package com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.mappers;

import com.itau.desafio.vendas.domain.model.Vehicle;
import com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.documents.VehicleDocument;

public class VehiclePersistenceMapper {

    public Vehicle toDomainModel(VehicleDocument document) {
        if (document == null) {
            return null;
        }
        return Vehicle.reconstitute(
                document.getModel(),
                document.getCost(),
                document.getManufactureYear(),
                document.getCreatedAt(),
                document.getCreatedBy(),
                document.getLastModifiedAt(),
                document.getLastModifiedBy()
        );
    }

    public VehicleDocument fromDomainModel(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }
        return VehicleDocument.builder()
                .model(vehicle.getModel())
                .cost(vehicle.getCost())
                .manufactureYear(vehicle.getManufactureYear())
                .createdAt(vehicle.getCreatedAt())
                .createdBy(vehicle.getCreatedBy())
                .lastModifiedAt(vehicle.getLastModifiedAt())
                .lastModifiedBy(vehicle.getLastModifiedBy())
                .build();
    }
}