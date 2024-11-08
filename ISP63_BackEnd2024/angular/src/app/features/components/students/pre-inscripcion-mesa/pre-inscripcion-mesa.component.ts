import { Component } from '@angular/core';

import { inscripcion } from '../../../../modelo/llamados/inscripcion';
import { CommonModule } from '@angular/common';

import { InfoLlamadosService } from '../../../../services/info-llamados.service';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-pre-inscripcion-mesa',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './pre-inscripcion-mesa.component.html',
  styleUrl: './pre-inscripcion-mesa.component.css'
})
export class PreInscripcionMesaComponent {


  datosInscripcion!: inscripcion[];
  datosInscripcionSearch!: inscripcion[];

  

  constructor(private infoLlamadosService: InfoLlamadosService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.infoLlamadosService.ObtenerDatos().subscribe((data: inscripcion[]) => {
      this.datosInscripcion = data;
      this.datosInscripcionSearch = data;
      console.log(this.datosInscripcion);
    });
  }

/*
 

   // busqueda de alumno por texto
   buscarAlumno(event: any) {
    const textoBusqueda = event.target.value.toLowerCase();

    this.datosInscripcion = this.datosInscripcionSearch?.filter(inscripcion =>
      this.normalizeString(inscripcion.anio).toLowerCase().startsWith(textoBusqueda) ||
      this.normalizeString(inscripcion.asignatura).toLowerCase().startsWith(textoBusqueda) ||
      this.normalizeString(inscripcion.carrera).toLowerCase().startsWith(textoBusqueda) ||
      this.normalizeString(inscripcion.tribunal).toLowerCase().startsWith(textoBusqueda) ||
      this.normalizeString(inscripcion.fechayHora).toLowerCase().startsWith(textoBusqueda) ||
      this.normalizeString(inscripcion.llamado).toLowerCase().startsWith(textoBusqueda) 
    ) ?? [];
  }

  // permite la busqueda sin acentos
normalizeString(text: string): string {
  return text.normalize('NFD').replace(/[\u0300-\u036f]/g, "");
}
*/


removeAccents(text: string): string {
  return text.normalize('NFD').replace(/[\u0300-\u036f]/g, "");
}

  // busqueda de alumno por texto
       buscarMesa(event: any) {
        const textoBusqueda = event.target.value.toLowerCase();
  
      let mesasFiltro = this.datosInscripcion?.filter(inscripcion =>
        this.removeAccents(inscripcion.asignatura.toLowerCase()).includes(textoBusqueda) ||
        this.removeAccents(inscripcion.carrera.toLowerCase()).includes(textoBusqueda) ||
        inscripcion.fechayHora.toString().includes(textoBusqueda) ||
        this.removeAccents(inscripcion.tribunal.toLowerCase()).includes(textoBusqueda) ||
        this.removeAccents(inscripcion.llamado.toLowerCase()).includes(textoBusqueda)
      ) ?? [];
    
  
      this.datosInscripcionSearch = mesasFiltro;
    }
  
 
// alerta muestra materias a las q se inscribio
showAlert = false;
selectedMaterias: { materia: string, fecha: string }[] = []; // Materias seleccionadas con fecha

// Cerrar alerta
closeAlert() {
  this.showAlert = false;
}

//seleccion de mesas 

toggleSelectAll(event: any) {
  const isChecked = event.target.checked;
  
  // Recorremos todas las mesas y cambiamos su estado de selección
  this.datosInscripcion.forEach(mesa => {
    mesa.seleccionada = isChecked;
  });
}

toggleMesaSelection(mesa: any) {
  mesa.seleccionada = !mesa.seleccionada;  // Cambiamos el estado seleccionado de la mesa
}



//guardar inscripcion 
submit() {
  // Obtenemos los ids de las mesas seleccionadas
  const mesasSeleccionadas = this.datosInscripcion
    .filter(mesa => mesa.seleccionada)
    .map(mesa => mesa.id);

  // Validación: Si no se seleccionaron mesas
  if (mesasSeleccionadas.length === 0) {
    alert('Por favor, seleccione al menos una mesa.');
    return;
  }

  // Guardamos las materias seleccionadas para la alerta (materia y fecha)
  this.selectedMaterias = this.datosInscripcion
    .filter(mesa => mesa.seleccionada) 
    .map(mesa => ({ materia: mesa.asignatura, fecha: mesa.fechayHora }));

  // Mostrar la alerta modal
  this.showAlert = true;

  // Estado de la inscripción
  const estado = 'PENDIENTE';

  const materiasHtml = this.selectedMaterias.map(materia => 
    `<li class="m-2"> ${materia.materia} (${materia.fecha})</li>`
  ).join('');

  // Agregar la notificación de éxito aquí
  Swal.fire({
    icon: 'success',
    title: 'Pre-inscripción registrada con éxito',
    html: `<ul>${materiasHtml}</ul>`, 
    confirmButtonText: 'OK'
  });
  this.router.navigate(['/estudiante-dashboard']);


  // Envía los ids de las mesas seleccionadas al servicio
  this.infoLlamadosService.saveInscripcion(mesasSeleccionadas, estado).subscribe(
    response => {
      console.log('Inscripción guardada exitosamente', response);
    },
    error => {
      console.error('Error al guardar la inscripción', error);
    }
  );
}






}
