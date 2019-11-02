package com.sap.cloud.s4hana.examples.addressmgr.commands;

import org.slf4j.Logger;

import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import com.sap.cloud.sdk.frameworks.hystrix.HystrixUtil;
import com.sap.cloud.sdk.s4hana.connectivity.ErpCommand;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddress;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.BusinessPartnerService;

public class CreateAddressCommand extends ErpCommand<BusinessPartnerAddress>{
    private static final Logger logger = CloudLoggerFactory.getLogger(CreateAddressCommand.class);

    private final BusinessPartnerService service;
    private final BusinessPartnerAddress addressToCreate;

    public CreateAddressCommand(final BusinessPartnerService service, final BusinessPartnerAddress addressToCreate) {
        //Al convertir el comando CreateAddresCommand en un Hystrix, se tiene que cambiar el constructor 
        //super(CreateAddressCommand.class);  //Sería el más sencillo. Todas las clases se ejecutan con la misma clave y se aplica configuración por defecto
        //Con este se define el tiempo máximo de espera a 10 segundos
        super(HystrixUtil.getDefaultErpCommandSetter
        (CreateAddressCommand.class, 
         HystrixUtil.getDefaultErpCommandProperties().withExecutionTimeoutInMilliseconds(10000))
        );

        this.service = service;
        this.addressToCreate = addressToCreate;
    }

    //public BusinessPartnerAddress execute() throws Exception {
        // TODO: Replace with Virtual Data Model query
    //    return service.createBusinessPartnerAddress(addressToCreate).execute();
    //}

    @Override
    protected BusinessPartnerAddress run() throws Exception {
        // TODO: Replace with Virtual Data Model query
        return service.createBusinessPartnerAddress(addressToCreate).execute();
    }
}
