package app.usecase

interface CpuCoresUseCase {
    operator fun invoke(): Int
}

expect fun getCpuCoresUseCase(): CpuCoresUseCase