package it.polito.wa2.traveler_service.security


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class WebSecurityConfig: WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var authenticationJwtTokenFilter: JwtAuthenticationTokenFilter


    /**
     * We override the configure(HttpSecurity http) method from WebSecurityConfigurerAdapter interface.
     * It tells Spring Security how we configure CORS and CSRF, when we want to require all users to be authenticated
     * or not, which filter (AuthTokenFilter) and when we want it to work (filter before
     * UsernamePasswordAuthenticationFilter), which Exception Handler is chosen (AuthEntryPointJwt).
     */
    override fun configure(http: HttpSecurity) {


        //without this, always return 403 Forbidden for any request
        http.csrf().disable() // disable csrf

        http.cors()


        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        http.authorizeRequests()
                .anyRequest()
                .authenticated()



        //esplicito che voglio aggiungere un filtro da eseguire prima di un certo filtro
        //(quindi specifico anche l'ordine così facendo)
        //riceve due parametri : il filtro implementato, qual è il filtro prima del quale eseguire questo filtro aggiunto
        //quindi gli sto dicendo : prima di eseguire il filtro di autenticazione basato su username e password (che di
        //solito, di base, è il primo della catena di filtri), esegui quest'altro filtro, il quale verifica se è valido
        //il JWT, e se è valido, inietta nel SecurityContext un Authentication già processata (in quanto noi creiamo e
        //inseriamo un Authentication il cui metodo isAuthenticated() ritorna true :  e da quanto ho capito poi, il filtro
        //a seguire, e cioè quello dell'Autenticazione, trova un Authentication già processata (isAuth  = true) e quindi
        //riesce a passare il filtro di autenticazione (quest'ultima parte è ciò che ho dedotto io)
        http.addFilterBefore(authenticationJwtTokenFilter,
                UsernamePasswordAuthenticationFilter::class.java)
    }

    /*@Bean
    override fun authenticationManagerBean(): AuthenticationManager? {
        return super.authenticationManagerBean()
    }*/

}