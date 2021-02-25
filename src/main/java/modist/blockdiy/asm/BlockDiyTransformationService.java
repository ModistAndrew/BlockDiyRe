package modist.blockdiy.asm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;

public class BlockDiyTransformationService implements ITransformationService {
	public String name() {
		return "BlockDiyTransformationService";
	}

	public void initialize(IEnvironment environment) {
	}

	public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException {
	}

	@SuppressWarnings("rawtypes")
	public List<ITransformer> transformers() {
		return Arrays.asList(new BlockDiyTransformer());
		//return Collections.EMPTY_LIST;
	}

	@Override
	public void beginScanning(IEnvironment environment) {
	}
}
