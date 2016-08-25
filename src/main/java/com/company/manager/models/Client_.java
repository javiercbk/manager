package com.company.manager.models;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Client.class)
public class Client_ extends Addressable_{
    public static volatile ListAttribute<Client, Provider> providers;
}
