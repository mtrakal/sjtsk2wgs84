package app.usecase

class CpuCoresUseCaseImpl : CpuCoresUseCase {
    override operator fun invoke(): Int = Runtime.getRuntime().availableProcessors()
}

actual fun getCpuCoresUseCase(): CpuCoresUseCase = CpuCoresUseCaseImpl()