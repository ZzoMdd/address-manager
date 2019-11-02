package com.sap.cloud.s4hana.examples.addressmgr.commands;

import com.sap.cloud.sdk.s4hana.connectivity.CachingErpCommand;
import com.sap.cloud.sdk.s4hana.datamodel.odata.helper.Order;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartner;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.BusinessPartnerService;

public class GetAllBusinessPartnersCommand extends CachingErpCommand<List<BusinessPartner>>{

    private static final Logger logger = CloudLoggerFactory.getLogger(GetAllBusinessPartnersCommand.class);

    private static final String CATEGORY_PERSON = "1";

    //Hay que inicializar la cache. Se utiliza la libreria Guava de Google. Se tiene qye declarar como estática o de lo contrario,
    //cada instancia crearía su cache por lo que no serviría de mucho.

    
    private static final Cache<CacheKey, List<BusinessPartner>> 
    //Cache con parámetros por defecto:
    //cache = CacheBuilder.newBuilder().build();
    
    //Cache indicando el número máximo de elementos y el tiempo en el que expira
    cache = CacheBuilder
            .newBuilder()
            .maximumSize(50)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private final BusinessPartnerService service;

    public GetAllBusinessPartnersCommand(final BusinessPartnerService service) {
        super(GetAllBusinessPartnersCommand.class);
        this.service = service;
    }

    //Al cambiar el comando a un Hystrix (Cacheable), hay que renombrar el método de ejecución y sobreescribirlo. Se puede tambier cambiar la visibilidad a protegido
    //public List<BusinessPartner> execute() throws Exception {
    @Override
    protected List<BusinessPartner> runCacheable() throws Exception {
        // TODO: Replace with Virtual Data Model query
        return service.getAllBusinessPartner()
        .select(BusinessPartner.BUSINESS_PARTNER, BusinessPartner.LAST_NAME, BusinessPartner.FIRST_NAME)
        .filter(BusinessPartner.BUSINESS_PARTNER_CATEGORY.eq(CATEGORY_PERSON))
        .orderBy(BusinessPartner.LAST_NAME, Order.ASC)
        .execute();
    }

    //Obligatorio implementarlo con CachingErpCommand
    @Override
    protected Cache<CacheKey, List<BusinessPartner>> getCache() {
        // TODO Auto-generated method stub
        return this.cache;
    }


    //Implementar este método es opcional. Si no se implenta, la clave de cache se genera automáticamente en base 
    //al usuario, el tenant y la configuración de contexto del ERP
    //Se va a implementar para que todos los usuarios compartan la misma cache, por lo que el usuario no se tendrá en cuenta, solo el tenant.
    //Si tuvieramos filtros tambien habría que añadirlos para que los resultados de la cache los tuvieran en cuenta. 
    //Por otro lado, dejar al usuario de lado y basarse sólo en el tenant, no tendría sentido si los resultados dependieran
    //de alguna forma del usuario, como por ejemplo autorizaciones
    @Override
    protected CacheKey getCommandCacheKey() {
        // TODO Auto-generated method stub
        return CacheKey.ofTenantIsolation();
    }


    //Este método se ejecuta si falla el comando y se devuelve algo vacío
    @Override
    protected List<BusinessPartner> getFallback() {
        // TODO Auto-generated method stub
        logger.warn("Fallback called because of exception:", getExecutionException());
        return Collections.emptyList();
    }

}
