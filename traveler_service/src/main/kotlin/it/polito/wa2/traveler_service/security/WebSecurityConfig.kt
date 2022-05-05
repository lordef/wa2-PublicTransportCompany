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

    override fun configure(http: HttpSecurity) {


        //without this, always return 403 Forbidden for any request
        http
                .csrf().disable() // disable csrf //TODO da vedere se serve


        //TODO servono i cookie? Se si, vanno sul login?
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        http.authorizeRequests()
                .anyRequest()
                .authenticated()

        http.addFilterBefore(authenticationJwtTokenFilter,
                UsernamePasswordAuthenticationFilter::class.java)
    }

    /*@Bean
    override fun authenticationManagerBean(): AuthenticationManager? {
        return super.authenticationManagerBean()
    }*/

}