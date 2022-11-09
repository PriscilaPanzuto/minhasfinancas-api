package com.priscilapanzuto.minhasfinancas.model.repository;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.priscilapanzuto.minhasfinancas.model.entity.Usuario;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioRepositoryTest {
	
	@Autowired
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		Usuario usuario = Usuario.builder().nome("usuario").email("usuario@email.com").senha("123456").build();
		entityManager.persist(usuario);
		
		boolean result = repository.existsByEmail("usuario@email.com");
		
		assertThat(result).isTrue();
	
		
	}
	
	@Test
	public void deveRetornarFalsoCasoNaoEncontreOEmail() {
		
		boolean result = repository.existsByEmail("usuario@email.com");
		
		assertThat(result).isFalse();
	}
	
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		//cenario
		Usuario usuarioSalvo = criarUsuario();
		
		//acao
		entityManager.persist(usuarioSalvo);
		
		//verificacao
		assertThat(usuarioSalvo.getId()).isNotNull();
	}
	
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		//cenario
		Usuario usuarioSalvo = criarUsuario();
		
		//acao
		entityManager.persist(usuarioSalvo);
		
		//verificacao
		assertThat(repository.findByEmail("priscila@priscila.com").isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioQuandoNaoEncontrarUmUsuarioPorEmailNaBase() {		
		
		assertThat(repository.findByEmail("priscila@priscila.com").isPresent()).isFalse();
	}
	
	public static Usuario criarUsuario() {
		return Usuario.builder().nome("priscila")
				.email("priscila@priscila.com")
				.senha("priscila")
				.build();
	}
	
}
