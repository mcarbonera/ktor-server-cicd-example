package com.evennt.infrastructure.di

import com.evennt.application.auth.useCases.*
import com.evennt.application.services.AuthGenerationService
import com.evennt.application.services.AuthVerificationService
import com.evennt.application.services.impl.JwtAuthGenerationService
import com.evennt.application.services.impl.JwtAuthVerificationService
import com.evennt.application.useCases.ListUsersUseCase
import com.evennt.core.Settings
import com.evennt.domain.repositories.TokenRevocationRepository
import com.evennt.domain.repositories.UserRepository
import com.evennt.domain.services.AuthTokenService
import com.evennt.domain.services.HashingService
import com.evennt.infrastructure.persistence.memory.TokenRevocationMemoryRepository
import com.evennt.infrastructure.persistence.sql.repositories.UserSqlRepository
import com.evennt.infrastructure.services.BCryptHashingService
import com.evennt.infrastructure.services.JwtService
import com.evennt.presentation.api.auth.handlers.*
import org.koin.dsl.module

val appModule =
    module {
        // repositories
        single<UserRepository> { UserSqlRepository() }
        single<TokenRevocationRepository> { TokenRevocationMemoryRepository() }

        // domain services (infra implementations)
        single<HashingService> { BCryptHashingService() }
        single<AuthTokenService> {
            JwtService(
                issuer = Settings.JWT_ISSUER,
                audience = Settings.JWT_AUDIENCE,
            )
        }

        // application services
        single<AuthGenerationService> { JwtAuthGenerationService(get()) }
        single<AuthVerificationService> {
            JwtAuthVerificationService(
                get(),
                get(),
                get(),
            )
        }

        // use cases
        single<ListUsersUseCase> { ListUsersUseCase(get()) }
        single<SignUpUseCase> { SignUpUseCase(get(), get(), get()) }
        single<SignInUseCase> { SignInUseCase(get(), get(), get()) }
        single<SignOutUseCase> { SignOutUseCase(get(), get()) }
        single<GetAuthUserUseCase> { GetAuthUserUseCase(get()) }

        // route handlers
        single<SignUpHandler> { SignUpHandler(get()) }
        single<SignInHandler> { SignInHandler(get()) }
        single<SignOutHandler> { SignOutHandler(get()) }
        single<GetAuthUserHandler> { GetAuthUserHandler(get()) }
    }
