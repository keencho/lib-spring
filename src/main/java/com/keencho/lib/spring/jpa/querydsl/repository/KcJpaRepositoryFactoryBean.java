package com.keencho.lib.spring.jpa.querydsl.repository;

import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.RepositoryFragment;
import org.springframework.lang.NonNull;

// https://stackoverflow.com/questions/53083047/replacing-deprecated-querydsljparepository-with-querydsljpapredicateexecutor-fai/53960209#53960209
// https://stackoverflow.com/questions/65018796/why-do-i-need-a-fragment-interface-for-repositories-that-stand-on-their-own
public class KcJpaRepositoryFactoryBean<T extends Repository<S, ID>, S, ID> extends JpaRepositoryFactoryBean<T, S, ID> {
    public KcJpaRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected @NonNull RepositoryFactorySupport createRepositoryFactory(@NonNull EntityManager entityManager) {
        return new KcQueryDSLRepositoryFactory(entityManager);
    }

    private static class KcQueryDSLRepositoryFactory extends JpaRepositoryFactory {
        private final EntityManager entityManager;

        public KcQueryDSLRepositoryFactory(EntityManager entityManager) {
            super(entityManager);
            this.entityManager = entityManager;
        }

        @Override
        protected @NonNull RepositoryComposition.RepositoryFragments getRepositoryFragments(@NonNull RepositoryMetadata metadata) {
            var fragments = super.getRepositoryFragments(metadata);

            if (KcJpaRepository.class.isAssignableFrom(metadata.getRepositoryInterface())) {
                var entityInformation = this.getEntityInformation(metadata.getDomainType());
                var impl = new KcDefaultJPAQuery<>(entityInformation, this.entityManager);

                fragments = fragments.append(RepositoryFragment.implemented(impl));
            }

            return fragments;
        }
    }
}
