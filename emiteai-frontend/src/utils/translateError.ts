export const translateError = (error: any): string => {
    if (error.code === 'ECONNABORTED') return 'Tempo de resposta excedido.'
    if (error.message === 'Network Error') return 'Falha de conexão com o servidor.'
    if (error.response) {
      const { status, data } = error.response
      const backendMsg =
        typeof data === 'string'
          ? data
          : data?.mensagem || data?.message || data?.error
      if (
        backendMsg?.includes('ConstraintViolationException') ||
        backendMsg?.includes('duplicate key')
      )
        return 'Registro já existe.'
      if (backendMsg?.includes('DataIntegrityViolationException'))
        return 'Violação de integridade de dados.'
      switch (status) {
        case 400:
          return backendMsg || 'Requisição inválida.'
        case 401:
          return 'Não autorizado.'
        case 403:
          return 'Acesso negado.'
        case 404:
          return 'Recurso não encontrado.'
        case 409:
          return backendMsg || 'Conflito de dados.'
        case 422:
          return backendMsg || 'Dados inválidos.'
        case 500:
          return backendMsg || 'Erro interno no servidor.'
        default:
          return backendMsg || `Erro inesperado (${status}).`
      }
    }
    return 'Erro desconhecido.'
  }
  