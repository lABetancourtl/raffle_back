package com.aba.raffle.proyecto.model.documents;

import com.aba.raffle.proyecto.model.enums.EstadoUsuario;
import com.aba.raffle.proyecto.model.enums.Role;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private EstadoUsuario estadoUsuario;
    private String fechaRegistro;
}
