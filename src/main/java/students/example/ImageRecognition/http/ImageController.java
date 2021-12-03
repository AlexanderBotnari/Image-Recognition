package students.example.ImageRecognition.http;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	public @ResponseBody byte[] getImageWithMediaType() throws IOException {
		// InputStream in = new
		// ClassPathResource("static/images/detected.png").getInputStream();
		// URL url = new URL("processed/images/detected.png");
		// byte[] data = toByteArray(in);
		// return data;
		InputStream in = new BufferedInputStream(new FileInputStream("processed/images/detected.png"));
		byte[] data = IOUtils.toByteArray(in);
		return data;
	}

	@PostMapping("/upload")
	@ResponseBody
	public DetectedObjects uploadImage(@RequestParam("image") MultipartFile file)
			throws IOException, ModelException, TranslateException {

		String uploadDirectoryName = "processed/uploads/";
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
