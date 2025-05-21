import React, { useState } from 'react'
import {
  Box,
  Button,
  CircularProgress,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
  Tooltip,
  LinearProgress,
  useTheme,
  TablePagination,
  Grid
} from '@mui/material'
import Papa from 'papaparse'
import { toast } from 'react-toastify'
import FileDownloadIcon from '@mui/icons-material/FileDownload'
import RefreshIcon from '@mui/icons-material/Refresh'
import api from '../api'
import { translateError } from 'utils/translateError'

const ReportPage: React.FC = () => {
  const theme = useTheme()
  const [loading, setLoading] = useState(false)
  const [generating, setGenerating] = useState(false)
  const [rows, setRows] = useState<string[][]>([])
  const [csvBlob, setCsvBlob] = useState<Blob | null>(null)
  const [page, setPage] = useState(0)
  const [rowsPerPage, setRowsPerPage] = useState(10)

  const saveCsvToDisk = (blob: Blob, filename = 'relatorio-pessoas.csv') => {
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = filename
    a.click()
    URL.revokeObjectURL(url)
  }

  const waitCsv = async (tentativas = 0): Promise<void> => {
    try {
      const { data } = await api.get<Blob>('/api/pessoas/relatorio/latest', {
        responseType: 'blob',
        validateStatus: s => s === 200 || s === 404
      })

      if (data.type === 'application/octet-stream') {
        setCsvBlob(data)
        const text = await data.text()
        const parsed = Papa.parse<string[]>(text, { skipEmptyLines: true }).data
        setRows(parsed as string[][])
        setGenerating(false)
        toast.success('Relatório gerado com sucesso')
      } else {
        if (tentativas < 10) setTimeout(() => waitCsv(tentativas + 1), 2000)
        else {
          setGenerating(false)
          toast.warn('Tempo limite excedido ao gerar relatório')
        }
      }
    } catch (error: any) {
      setGenerating(false)
      toast.error(translateError(error))
    }
  }

  const generate = async (): Promise<void> => {
    try {
      setGenerating(true)
      setLoading(true)
      setCsvBlob(null)
      setRows([])
      await api.post('/api/pessoas/relatorio')
      toast.info('Gerando relatório...')
      waitCsv()
    } catch (error: any) {
      setGenerating(false)
      toast.error(translateError(error))
    } finally {
      setLoading(false)
    }
  }

  const handleChangePage = (event: unknown, newPage: number) => {
    setPage(newPage)
  }

  const handleChangeRowsPerPage = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setRowsPerPage(parseInt(event.target.value, 10))
    setPage(0)
  }

  // Calculate the paginated rows
  const paginatedRows = rows.slice(1).slice(
    page * rowsPerPage,
    page * rowsPerPage + rowsPerPage
  )

  return (
    <Box sx={{ p: 4, maxWidth: '95vw', margin: '0 auto' }}>
      <Grid container justifyContent="space-between" alignItems="center" mb={3}>
        <Grid item>
          <Typography variant="h4" component="h1" fontWeight="bold">
            Relatório de Pessoas
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Visualize e exporte os dados cadastrais
          </Typography>
        </Grid>
        <Grid item>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <Tooltip title="Gerar novo relatório">
              <Button
                variant="contained"
                onClick={generate}
                disabled={loading}
                startIcon={
                  loading ? (
                    <CircularProgress size={20} color="inherit" />
                  ) : (
                    <RefreshIcon />
                  )
                }
                sx={{
                  px: 3,
                  py: 1,
                  borderRadius: 2,
                  boxShadow: theme.shadows[2],
                  '&:hover': {
                    boxShadow: theme.shadows[4]
                  }
                }}
              >
                {generating ? 'Gerando...' : 'Gerar Relatório'}
              </Button>
            </Tooltip>

            <Tooltip title="Baixar relatório em CSV">
              <Button
                variant="outlined"
                onClick={() => csvBlob && saveCsvToDisk(csvBlob)}
                disabled={!csvBlob}
                startIcon={<FileDownloadIcon />}
                sx={{
                  px: 3,
                  py: 1,
                  borderRadius: 2,
                  borderWidth: 2,
                  '&:hover': {
                    borderWidth: 2
                  }
                }}
              >
                Exportar CSV
              </Button>
            </Tooltip>
          </Box>
        </Grid>
      </Grid>

      {generating && (
        <Box sx={{ width: '100%', mb: 2 }}>
          <LinearProgress color="primary" />
          <Typography variant="caption" color="text.secondary">
            Preparando relatório...
          </Typography>
        </Box>
      )}

      {rows.length > 0 && (
        <Paper
          elevation={3}
          sx={{
            borderRadius: 4,
            overflow: 'hidden',
            border: `1px solid ${theme.palette.divider}`,
            width: '100%'
          }}
        >
          <TableContainer sx={{ maxHeight: 'calc(100vh - 250px)' }}>
            <Table stickyHeader size="medium">
              <TableHead>
                <TableRow>
                  {rows[0].map((h, i) => (
                    <TableCell
                      key={i}
                      sx={{
                        backgroundColor: theme.palette.primary.main,
                        color: theme.palette.common.white,
                        fontWeight: 600,
                        fontSize: '0.875rem',
                        py: 2,
                        borderRight: i < rows[0].length - 1 ? `1px solid ${theme.palette.primary.light}` : 'none'
                      }}
                    >
                      {h}
                    </TableCell>
                  ))}
                </TableRow>
              </TableHead>
              <TableBody>
                {paginatedRows.map((r, i) => (
                  <TableRow
                    key={i}
                    hover
                    sx={{
                      '&:nth-of-type(even)': {
                        backgroundColor: theme.palette.action.hover
                      },
                      '&:hover': {
                        backgroundColor: theme.palette.action.selected
                      }
                    }}
                  >
                    {r.map((c, j) => (
                      <TableCell
                        key={j}
                        sx={{
                          py: 1.5,
                          borderRight: j < r.length - 1 ? `1px solid ${theme.palette.divider}` : 'none',
                          whiteSpace: 'nowrap',
                          overflow: 'hidden',
                          textOverflow: 'ellipsis',
                          maxWidth: 200
                        }}
                      >
                        {c}
                      </TableCell>
                    ))}
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
          <TablePagination
            rowsPerPageOptions={[10, 25, 50, 100]}
            component="div"
            count={rows.length - 1}
            rowsPerPage={rowsPerPage}
            page={page}
            onPageChange={handleChangePage}
            onRowsPerPageChange={handleChangeRowsPerPage}
            labelRowsPerPage="Linhas por página:"
            labelDisplayedRows={({ from, to, count }) =>
              `${from}-${to} de ${count}`
            }
            sx={{
              borderTop: `1px solid ${theme.palette.divider}`,
              '& .MuiTablePagination-toolbar': {
                minHeight: 56
              }
            }}
          />
        </Paper>
      )}

      {!generating && rows.length === 0 && (
        <Paper
          elevation={0}
          sx={{
            p: 4,
            textAlign: 'center',
            backgroundColor: theme.palette.grey[50],
            borderRadius: 4
          }}
        >
          <Typography variant="h6" color="text.secondary" gutterBottom>
            Nenhum relatório gerado
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Clique em "Gerar Relatório" para criar um novo relatório
          </Typography>
        </Paper>
      )}
    </Box>
  )
}

export default ReportPage