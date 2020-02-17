package org.walter.app.configuration.jpa.product;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.walter.app.configuration.jpa.MultiTenantCurrentTenantIdentifierResolver;
import org.walter.app.constant.MultiTenantDataSourceTypeEnum;
import org.walter.base.entity.JpaAclTenantDataSource;
import org.walter.base.tenant.AbstractMultiTenantConfig;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Fund(清结算)的多租户db配置
 */
//@Configuration
@EnableJpaRepositories(
        basePackages={"org.walter.app.repository.product"},
        entityManagerFactoryRef = "productMultiTenantEntityManagerFactory",
        transactionManagerRef = "productMultiTenantJpaTransactionManager")
public class MultiTenantProductConfig extends AbstractMultiTenantConfig {
    @Autowired
    private MultiTenantProductConnectionProviderImpl multiTenantProductConnectionProvider;
    @Autowired
    private MultiTenantCurrentTenantIdentifierResolver multiTenantCurrentTenantIdentifierResolver;

    @Bean
    public Map<String, DataSource> productMultiTenantRoutingDataSource(){
        Map<String, DataSource> targetDataSources = initMultiTenantDataSourceMap();
        List<JpaAclTenantDataSource> jpaAclTenantDataSourceList = aclTenantDataSourceRepository
                .findAllByDataSourceId(MultiTenantDataSourceTypeEnum.PRODUCT.getCode());
        for (JpaAclTenantDataSource tenantDataSource : jpaAclTenantDataSourceList) {
            targetDataSources.put(tenantDataSource.getTenantId(), createDataSource(tenantDataSource));
        }
        return targetDataSources;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean productMultiTenantEntityManagerFactory(){
        Properties properties = new Properties();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        properties.put(Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
        properties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantProductConnectionProvider);
        properties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, multiTenantCurrentTenantIdentifierResolver);

        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setPackagesToScan("org.walter.app.entity.product");
        bean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        bean.setJpaProperties(properties);
        return bean;
    }

    @Bean
    public PlatformTransactionManager productMultiTenantJpaTransactionManager() {
        return new JpaTransactionManager(productMultiTenantEntityManagerFactory().getObject());
    }
}
