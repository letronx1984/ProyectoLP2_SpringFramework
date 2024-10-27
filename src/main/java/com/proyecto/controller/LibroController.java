package com.proyecto.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.proyecto.model.AutorEntity;
import com.proyecto.model.CategoriaEntity;
import com.proyecto.model.EditorialEntity;
import com.proyecto.model.LibroCategoriaEntity;
import com.proyecto.model.LibroEntity;
import com.proyecto.service.AutorService;
import com.proyecto.service.CategoriaService;
import com.proyecto.service.EditorialService;
import com.proyecto.service.LibroCategoriaService;
import com.proyecto.service.LibroService;
import com.proyecto.service.impl.PdfService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/libro")
public class LibroController {

	@Autowired
	private LibroService libroService;
	@Autowired
	private AutorService autorService;
	@Autowired
	private CategoriaService categoriaService;
	@Autowired
	private LibroCategoriaService libroCategoriaService;
	@Autowired
	private PdfService pdfService;
	@Autowired
	private EditorialService editorialService;

	@GetMapping("/listado")
	public String libros(Model model, HttpSession sesion) {
		List<CategoriaEntity> listarCategorias = categoriaService.listadoCategorias();
		List<LibroEntity> listaLibros = libroService.listadoLibros();
		model.addAttribute("categorias", listarCategorias);
		model.addAttribute("libros", listaLibros);
		return "catalogoAdmi";
	}

	@GetMapping("/registrar")
	public String mostrarFormularioAgregarLibro(Model model) {

		List<AutorEntity> listarAutores = autorService.listadoAutores();
		List<CategoriaEntity> listarCategoriaLibros = categoriaService.listadoCategorias();
		List<LibroCategoriaEntity> litarListas = libroCategoriaService.listarTodosCategoriaLibro();
		List<EditorialEntity> listarEditoriales = editorialService.listadoEditoriales();

		model.addAttribute("autores", listarAutores);
		model.addAttribute("categorias", listarCategoriaLibros);
		model.addAttribute("editoriales", listarEditoriales);
		model.addAttribute("libro", new LibroEntity());
		return "pagLibros";
	}

	@PostMapping("/registrar")
	public String registrarLibro(@ModelAttribute("libro") LibroEntity libroEntity,
			Model model, @RequestParam("foto") MultipartFile imagen) {
		libroService.registrarLibro(libroEntity, imagen);
		return "redirect:/libro/listado";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminarLibro(@PathVariable("id") String id) {
		libroService.eliminarLibro(id);
		return "redirect:/libro/listado";
	}

	@GetMapping("/pdf_libros")
	public ResponseEntity<InputStreamResource> generarPDF(HttpSession sesion) throws IOException {
		List<LibroEntity> lstLibros = libroService.listadoLibros();

		Map<String, Object> datosPdf = new HashMap<>();
		datosPdf.put("libros", lstLibros);

		ByteArrayInputStream pdfBytes = pdfService.generarPdf("libros_pdf", datosPdf);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=libros.pdf");

		return ResponseEntity.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_PDF)
				.body(new InputStreamResource(pdfBytes));

	}

	@GetMapping("/detalle/{ISBN}")
	public String obtenerInfoLibro(@PathVariable("ISBN") String isbn, Model model) {
		Optional<LibroEntity> libroOpt = libroService.buscarLibroPorISBN(isbn);
		if (libroOpt.isPresent()) {
			model.addAttribute("libro", libroOpt.get());
			return "libroInfoAdmin";
		} else {
			return "error/404";
		}
	}

}