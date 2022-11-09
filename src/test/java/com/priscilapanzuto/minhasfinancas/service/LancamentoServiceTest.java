package com.priscilapanzuto.minhasfinancas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.priscilapanzuto.minhasfinancas.exception.RegraNegocioException;
import com.priscilapanzuto.minhasfinancas.model.entity.Lancamento;
import com.priscilapanzuto.minhasfinancas.model.entity.Usuario;
import com.priscilapanzuto.minhasfinancas.model.enums.StatusLancamento;
import com.priscilapanzuto.minhasfinancas.model.enums.TipoLancamento;
import com.priscilapanzuto.minhasfinancas.model.repository.LancamentoRepository;
import com.priscilapanzuto.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.priscilapanzuto.minhasfinancas.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
public class LancamentoServiceTest {
	
	@SpyBean
	private LancamentoServiceImpl service;
	
	@MockBean
	private LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		//cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		//execucao
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		//verificacao
		assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		//cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);
		//execucao e verificacao
		catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
		
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		//cenario
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doNothing().when(service).validar(lancamentoSalvo);
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		//execucao
		service.atualizar(lancamentoSalvo);
		//verificacao
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}
	
	@Test
	public void deveLancarErroAoAtualizarLancamentoAindaNaoSalvo() {
		//cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		
		//execucao e verificacao
		catchThrowableOfType(() -> service.atualizar(lancamentoASalvar), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
		
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		//cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);

		//execucao
		service.deletar(lancamento);
		
		//verificacao
		Mockito.verify(repository).delete(lancamento);
	}
	
	@Test
	public void deveLancarErroAoDeletarLancamentoAindaNaoSalvo() {
		//cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		//execucao e verificacao
		catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
		
	}
	
	@Test
	public void deveFiltrarLancamento() {
		//cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		//execucao
		List<Lancamento> resultado = service.buscar(lancamento);
		
		//verificacao
		assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
	}
	
	@Test
	public void deveAtualizarUmStatus() {
		//cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento statusNovo = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
		
		//execucao
		service.atualizarStatus(lancamento, statusNovo);
		
		//verificacao
		assertThat(lancamento.getStatus()).isEqualTo(statusNovo);
		Mockito.verify(service).atualizar(lancamento);
	}
	
	@Test
	public void deveBuscarUmLancamentoPorId() {
		//cenario
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		//execucao
		Optional<Lancamento> resultado = service.buscarPorId(id);
		
		//verificacao
		assertThat(resultado.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioQuandoBuscarUmLancamentoInexistente() {
		//cenario
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		//execucao
		Optional<Lancamento> resultado = service.buscarPorId(id);
		
		//verificacao
		assertThat(resultado.isPresent()).isFalse();
	}
	
	@Test
	public void deveLancarErroAoValidarSeDescricaoNullOuvazia() {
		//cenario
		Lancamento lancamento = new Lancamento();
		
		//execucao e verificacao
		assertThrows(RegraNegocioException.class, () -> service.validar(lancamento), "Informe uma Descrição válida.");
		
		lancamento.setDescricao("Salario");
		assertThrows(RegraNegocioException.class,() -> service.validar(lancamento), "Informe um Mês válido.");
		
		lancamento.setMes(0);
		assertThrows(RegraNegocioException.class,() -> service.validar(lancamento), "Informe um Mês válido.");
		
		lancamento.setMes(13);
		assertThrows(RegraNegocioException.class,() -> service.validar(lancamento), "Informe um Mês válido.");
		
		lancamento.setMes(1);
		assertThrows(RegraNegocioException.class,() -> service.validar(lancamento), "Informe um Ano válido com 4 digitos.");
		
		lancamento.setAno(202);
		assertThrows(RegraNegocioException.class,() -> service.validar(lancamento), "Informe um Ano válido com 4 digitos.");
		
		lancamento.setAno(2022);
		assertThrows(RegraNegocioException.class,() -> service.validar(lancamento), "Informe um Usuário.");
		
		lancamento.setUsuario(new Usuario());
		assertThrows(RegraNegocioException.class,() -> service.validar(lancamento), "Informe um Usuário.");
		
		lancamento.getUsuario().setId(1l);
		assertThrows(RegraNegocioException.class,() -> service.validar(lancamento), "Informe um Valor válido.");
		
		lancamento.setValor(BigDecimal.ZERO);
		assertThrows(RegraNegocioException.class,() -> service.validar(lancamento), "Informe um Valor válido.");
		
		lancamento.setValor(BigDecimal.TEN);
		assertThrows(RegraNegocioException.class,() -> service.validar(lancamento), "Informe o Tipo de Lançamento.");
		
		lancamento.setTipo(TipoLancamento.RECEITA);
		
		assertDoesNotThrow(() -> service.validar(lancamento));
		
	}
	
	@Test
	public void deveObterSaldoPorUsuario() {
		Mockito.when(repository.obterSaldoPorTipoLancamentoEUsuario(1l, TipoLancamento.RECEITA))
				.thenReturn(BigDecimal.valueOf(500));
		Mockito.when(repository.obterSaldoPorTipoLancamentoEUsuario(1l, TipoLancamento.DESPESA))
				.thenReturn(BigDecimal.valueOf(300));
		
		BigDecimal resultado = service.obterSaldoPorUsuario(1l);
		
		assertThat(resultado).isEqualTo(BigDecimal.valueOf(200));
		
	}
}
