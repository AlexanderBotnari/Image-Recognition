package students.example.ImageRecognition.http;

import static org.apache.commons.compress.utils.IOUtils.toByteArray;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import ai.djl.ModelException;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.translate.TranslateException;

@Controller
@RequestMapping("/images")
public class ImageController {

	@Autowired
	ImageService imageService;

	private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

	@GetMapping("/page")
	public ModelAndView showPage() {
		ModelAndView view = new ModelAndView("page");
		return view;
	}

	@GetMapping(value = "/get", produces = MediaType.IMAGE_PNG_VALUE)
	public synchronized @ResponseBody byte[] getImageWithMediaType() throws IOException {
		InputStream in = new ClassPathResource("/detected.png").getInputStream();
		byte[] data = toByteArray(in);

		return data;
	}

	@PostMapping("/upload")
	@ResponseBody
	public DetectedObjects uploadImage(@RequestParam("image") MultipartFile file)
			throws IOException, ModelException, TranslateException {

		String uploadDirectoryName = "src/main/resources";
		Path fileName = Paths.get(file.getOriginalFilename());

		Path uploadDirPath = Paths.get(uploadDirectoryName);
		Path uploadFilePath = uploadDirPath.resolve(fileName);

		if (!Files.exists(uploadDirPath)) {
			Files.createDirectory(uploadDirPath);
		}

		InputStream is = file.getInputStream();

		Files.copy(is, uploadFilePath, StandardCopyOption.REPLACE_EXISTING);

		DetectedObjects detection = imageService.predict(uploadFilePath);
		logger.info("{}", detection);
		return detection;
	}
}
