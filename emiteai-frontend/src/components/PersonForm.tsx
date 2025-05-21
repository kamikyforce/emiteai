import React, { useState } from 'react'
import { useFormik } from 'formik'
import * as Yup from 'yup'
import {
  Box,
  TextField,
  Button,
  Grid,
  Typography,
  CircularProgress
} from '@mui/material'
import axios from 'axios'
import { toast } from 'react-toastify'
import api from '../api'
import { translateError } from 'utils/translateError'

interface Address {
  cep: string
  logradouro: string
  bairro: string
  localidade: string
  uf: string
  erro?: boolean
}

interface PersonFormData {
  nome: string
  telefone: string
  cpf: string
  cep: string
  logradouro: string
  numero: string
  complemento: string
  bairro: string
  cidade: string
  estado: string
}

const validationSchema = Yup.object({
  nome: Yup.string().required('Nome é obrigatório'),
  telefone: Yup.string()
    .required('Telefone é obrigatório')
    .matches(
      /^(\(\d{2}\))\s?(?:9\d{4}|\d{4})-\d{4}$/,
      'Telefone inválido (formato: (XX) 99999-9999)'
    ),
  cpf: Yup.string()
    .required('CPF é obrigatório')
    .matches(
      /^(\d{3}\.){2}\d{3}-\d{2}$/,
      'CPF deve estar no formato XXX.XXX.XXX-XX'
    ),
  cep: Yup.string()
    .required('CEP é obrigatório')
    .matches(/^\d{5}-\d{3}$/, 'CEP inválido (formato: XXXXX-XXX)'),
  numero: Yup.string().required('Número é obrigatório'),
  complemento: Yup.string()
})

export const PersonForm: React.FC = () => {
  const [loading, setLoading] = useState(false)
  const [cepLoading, setCepLoading] = useState(false)
  const [lastError, setLastError] = useState<string | null>(null)

  const showErrorToast = (error: any) => {
    const errorMessage = translateError(error)
    if (errorMessage !== lastError) {
      setLastError(errorMessage)
      toast.error(errorMessage, {
        onClose: () => setLastError(null)
      })
    }
  }

  const formik = useFormik<PersonFormData>({
    initialValues: {
      nome: '',
      telefone: '',
      cpf: '',
      cep: '',
      logradouro: '',
      numero: '',
      complemento: '',
      bairro: '',
      cidade: '',
      estado: ''
    },
    validationSchema,
    onSubmit: async (values, { resetForm, setSubmitting }) => {
      try {
        setLoading(true)
        const payload = {
          nome: values.nome.trim(),
          cpf: values.cpf.trim(),
          telefone: values.telefone.trim(),
          cep: values.cep.trim(),
          numero: values.numero.trim(),
          complemento: values.complemento.trim(),
          bairro: values.bairro.trim(),
          municipio: values.cidade.trim(),
          estado: values.estado.trim()
        }
        const { status } = await api.post('/api/pessoas', payload)
        if (status === 200 || status === 201) {
          toast.success('Pessoa cadastrada com sucesso')
          resetForm()
        }
      } catch (error: any) {
        showErrorToast(error)
      } finally {
        setLoading(false)
        setSubmitting(false)
      }
    }
  })

  const fetchAddress = async (cepNumbers: string) => {
    try {
      setCepLoading(true)
      const { data } = await axios.get<Address>(
        `https://viacep.com.br/ws/${cepNumbers}/json/`
      )
      if (data.erro) {
        throw new Error('CEP não encontrado')
      }
      const { bairro, localidade, uf, logradouro } = data
      formik.setFieldValue('logradouro', logradouro || '')
      formik.setFieldValue('bairro', bairro || '')
      formik.setFieldValue('cidade', localidade || '')
      formik.setFieldValue('estado', uf || '')
    } catch (error: any) {
      formik.setFieldError('cep', 'CEP não encontrado')
      formik.setFieldValue('logradouro', '')
      formik.setFieldValue('bairro', '')
      formik.setFieldValue('cidade', '')
      formik.setFieldValue('estado', '')
      showErrorToast(error)
    } finally {
      setCepLoading(false)
    }
  }

  const handleCepBlur = (e: React.FocusEvent<HTMLInputElement>) => {
    const onlyNumbers = e.target.value.replace(/\D/g, '')
    if (onlyNumbers.length === 8) fetchAddress(onlyNumbers)
  }

  const formatCpf = (value: string) =>
    value
      .replace(/\D/g, '')
      .slice(0, 11)
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d{1,2})$/, '$1-$2')

  const formatPhone = (value: string) =>
    value
      .replace(/\D/g, '')
      .slice(0, 11)
      .replace(/(\d{2})(\d)/, '($1) $2')
      .replace(/(\d{4,5})(\d)/, '$1-$2')

  const formatCep = (value: string) =>
    value.replace(/\D/g, '').slice(0, 8).replace(/(\d{5})(\d)/, '$1-$2')

  const handleCpfChange = (e: React.ChangeEvent<HTMLInputElement>) =>
    formik.setFieldValue('cpf', formatCpf(e.target.value))
  const handlePhoneChange = (e: React.ChangeEvent<HTMLInputElement>) =>
    formik.setFieldValue('telefone', formatPhone(e.target.value))
  const handleCepChange = (e: React.ChangeEvent<HTMLInputElement>) =>
    formik.setFieldValue('cep', formatCep(e.target.value))

  return (
    <Box
      component="form"
      onSubmit={formik.handleSubmit}
      noValidate
      sx={{ maxWidth: 800, m: '0 auto', p: 3 }}
    >
      <Typography variant="h4" gutterBottom>
        Cadastro de Pessoa Física
      </Typography>

      <Grid container spacing={3}>
        <Grid item xs={12}>
          <TextField
            fullWidth
            id="nome"
            name="nome"
            label="Nome Completo"
            value={formik.values.nome}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.nome && Boolean(formik.errors.nome)}
            helperText={formik.touched.nome && formik.errors.nome}
            disabled={loading}
            required
          />
        </Grid>

        <Grid item xs={12} sm={6}>
          <TextField
            fullWidth
            id="telefone"
            name="telefone"
            label="Telefone"
            value={formik.values.telefone}
            onChange={handlePhoneChange}
            onBlur={formik.handleBlur}
            error={formik.touched.telefone && Boolean(formik.errors.telefone)}
            helperText={formik.touched.telefone && formik.errors.telefone}
            disabled={loading}
            inputProps={{ maxLength: 15 }}
            required
          />
        </Grid>

        <Grid item xs={12} sm={6}>
          <TextField
            fullWidth
            id="cpf"
            name="cpf"
            label="CPF"
            value={formik.values.cpf}
            onChange={handleCpfChange}
            onBlur={formik.handleBlur}
            error={formik.touched.cpf && Boolean(formik.errors.cpf)}
            helperText={formik.touched.cpf && formik.errors.cpf}
            disabled={loading}
            inputProps={{ maxLength: 14 }}
            required
          />
        </Grid>

        <Grid item xs={12} sm={4}>
          <TextField
            fullWidth
            id="cep"
            name="cep"
            label="CEP"
            value={formik.values.cep}
            onChange={handleCepChange}
            onBlur={handleCepBlur}
            error={formik.touched.cep && Boolean(formik.errors.cep)}
            helperText={formik.touched.cep && formik.errors.cep}
            disabled={loading || cepLoading}
            inputProps={{ maxLength: 9 }}
            InputProps={{
              endAdornment: cepLoading ? (
                <CircularProgress color="inherit" size={20} />
              ) : null
            }}
            required
          />
        </Grid>

        <Grid item xs={12} sm={8}>
          <TextField
            fullWidth
            id="logradouro"
            name="logradouro"
            label="Logradouro"
            value={formik.values.logradouro}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={
              formik.touched.logradouro && Boolean(formik.errors.logradouro)
            }
            helperText={formik.touched.logradouro && formik.errors.logradouro}
            disabled={loading}
            required
          />
        </Grid>

        <Grid item xs={12} sm={4}>
          <TextField
            fullWidth
            id="numero"
            name="numero"
            label="Número"
            value={formik.values.numero}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.numero && Boolean(formik.errors.numero)}
            helperText={formik.touched.numero && formik.errors.numero}
            disabled={loading}
            required
          />
        </Grid>

        <Grid item xs={12} sm={8}>
          <TextField
            fullWidth
            id="complemento"
            name="complemento"
            label="Complemento"
            value={formik.values.complemento}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            disabled={loading}
          />
        </Grid>

        <Grid item xs={12} sm={4}>
          <TextField
            fullWidth
            id="bairro"
            name="bairro"
            label="Bairro"
            value={formik.values.bairro}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.bairro && Boolean(formik.errors.bairro)}
            helperText={formik.touched.bairro && formik.errors.bairro}
            disabled={loading}
            required
          />
        </Grid>

        <Grid item xs={12} sm={4}>
          <TextField
            fullWidth
            id="cidade"
            name="cidade"
            label="Cidade"
            value={formik.values.cidade}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.cidade && Boolean(formik.errors.cidade)}
            helperText={formik.touched.cidade && formik.errors.cidade}
            disabled={loading}
            required
          />
        </Grid>

        <Grid item xs={12} sm={4}>
          <TextField
            fullWidth
            id="estado"
            name="estado"
            label="Estado"
            value={formik.values.estado}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.estado && Boolean(formik.errors.estado)}
            helperText={formik.touched.estado && formik.errors.estado}
            disabled={loading}
            required
          />
        </Grid>
      </Grid>

      <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end' }}>
        <Button
          type="submit"
          variant="contained"
          color="primary"
          disabled={loading || cepLoading || formik.isSubmitting}
          startIcon={loading ? <CircularProgress size={20} color="inherit" /> : null}
          sx={{ minWidth: 120 }}
        >
          {loading ? 'Salvando…' : 'Salvar'}
        </Button>
      </Box>
    </Box>
  )
}