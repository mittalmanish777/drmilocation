package com.pge.drmi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableWebSecurity
@EnableSwagger2
public class SwaggerConfig extends WebSecurityConfigurerAdapter {

  
  private static String REALM="MY_TEST_REALM";
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
     auth.inMemoryAuthentication()
        .withUser("manish").password("password123").roles("USER")
        .and()
        .withUser("admin").password("pass123").roles("ADMIN");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests().antMatchers("/api/**").hasRole("USER")
    .and()
    .httpBasic().authenticationEntryPoint(getBasicAuthEntryPoint())
    .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);;
  }
  
  @Bean
  public CustomBasicAuthenticationEntryPoint getBasicAuthEntryPoint(){
      return new CustomBasicAuthenticationEntryPoint();
  }
  
  @Bean
  public Docket api() { 
      return new Docket(DocumentationType.SWAGGER_2)  
        .select()                                  
        .apis(RequestHandlerSelectors.any())              
        .paths(PathSelectors.any())                          
        .build();                                           
  }
  
}
