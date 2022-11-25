package com.keencho.lib.spring.jpa.querydsl.repository;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.RepositoryFragment;

import javax.persistence.EntityManager;

// https://stackoverflow.com/questions/53083047/replacing-deprecated-querydsljparepository-with-querydsljpapredicateexecutor-fai/53960209#53960209
// https://stackoverflow.com/questions/65018796/why-do-i-need-a-fragment-interface-for-repositories-that-stand-on-their-own
public class KcJpaRepositoryFactoryBean<T extends Repository<S, ID>, S, ID> extends JpaRepositoryFactoryBean<T, S, ID> {
    public KcJpaRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new KcQueryDSLRepositoryFactory(entityManager);
    }

    private static class KcQueryDSLRepositoryFactory extends JpaRepositoryFactory {
        private final EntityManager entityManager;

        public KcQueryDSLRepositoryFactory(EntityManager entityManager) {
            super(entityManager);
            this.entityManager = entityManager;
        }

        @Override
        protected RepositoryComposition.RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {
            var fragments = super.getRepositoryFragments(metadata);

            if (KcSearchQuery.class.isAssignableFrom(metadata.getRepositoryInterface())) {
                var entityInformation = this.getEntityInformation(metadata.getDomainType());

                var impl = super.instantiateClass(
                        KcDefaultJPAQuery.class,
                        entityInformation, this.entityManager
                );

                fragments = fragments.append(RepositoryFragment.implemented(impl));
            }

            return fragments;
        }
    }
}
