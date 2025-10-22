package com.aba.raffle.proyecto.model.entities;

import com.aba.raffle.proyecto.dto.WinnerDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sorteos_actas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SorteoActa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long raffleId;
    private LocalDateTime fechaEjecucion;
    private String semilla;
    private String hashGenerado;

    @ElementCollection
    @CollectionTable(name = "sorteo_ganadores", joinColumns = @JoinColumn(name = "sorteo_id"))
    @Column(name = "numero_ganador")
    private List<String> numerosGanadores;

    @ElementCollection
    @CollectionTable(name = "sorteo_acta_ganadores", joinColumns = @JoinColumn(name = "acta_id"))
    private List<WinnerEmbeddable> ganadores;



    private String archivoPdfUrl; // opcional, si luego lo subes a Cloudinary o guardas path local
}
