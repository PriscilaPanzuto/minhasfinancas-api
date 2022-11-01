package com.priscilapanzuto.minhasfinancas.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.priscilapanzuto.minhasfinancas.exception.ErroAutenticacao;
import com.priscilapanzuto.minhasfinancas.exception.RegraNegocioException;
import com.priscilapanzuto.minhasfinancas.model.entity.Usuario;
import com.priscilapanzuto.minhasfinancas.model.repository.UsuarioRepository;
import com.priscilapanzuto.minhasfinancas.service.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
public class UsuarioServiceTest {
	
	@SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;
	
	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		//cenario
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		
		//acao
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		//Verificacao
		assertDoesNotThrow(() -> service.autenticar(email, senha));
		
		Assertions.assertThat(service.autenticar(email, senha)).isNotNull();
		
	}
	
	@Test
	public void deveRetornarErroAutenticacaoEmailUsuarioNaoEncontrado() {
		
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com","1234"));
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuário nao encontrado para o email informado.");
	}
	
	@Test
	public void deveRetornarErroAutenticacaoSeASenhaUsuarioForInvalida() {
		//cenario
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).id(1l).build();
		
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com","1234"));
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida.");
	}
	
	@Test
	public void deveValidarEmailENaoLancarException() {
		
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

		assertDoesNotThrow(() -> service.validarEmail("email@email.com"));
		
	}
	
	@Test
	public void deveSalvarUsuarioComSucesso() {
		//cenario
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder().id(1l).nome("nome").email("email@email.com").senha("senha").build();
		
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		//acao
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		//verificacao
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
		
	}
	
	@Test
	public void deveLancarExceptionQuandoTentarSalvarUsuarioComEmailJaCadastrado() {
		//cenario
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().id(1l).email(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		Exception exception = null;
		
		//acao
		try {
			service.salvarUsuario(usuario);
		} catch (Exception e) {
			exception = e;
		}
		
		
		//verificacao
		Mockito.verify(repository, Mockito.never()).save(usuario);
		assertNotNull(exception);
		
	}
	
	@Test
	public void deveLancarExceptionAoValidarEmailJaCadastrado() {
		
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		assertThrows(RegraNegocioException.class, () -> service.validarEmail("email@email.com"));

	}

}
