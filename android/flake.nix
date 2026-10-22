{
  description = "Hermex Android development environment";
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-25.11";
  outputs = { self, nixpkgs }:
    let
      system = "x86_64-linux";
      pkgs = import nixpkgs { inherit system; config = { allowUnfree = true; android_sdk.accept_license = true; }; };
      sdk = (pkgs.androidenv.composeAndroidPackages {
        buildToolsVersions = [ "35.0.0" ]; platformVersions = [ "35" ];
        includeEmulator = false; includeSystemImages = false; includeNDK = false; includeCmake = false;
      }).androidsdk;
      gradle = pkgs.gradle;
    in {
      devShells.${system}.default = pkgs.mkShell {
        packages = [ pkgs.jdk21 pkgs.curl pkgs.unzip sdk gradle ];
        JAVA_HOME = "${pkgs.jdk21}";
        ANDROID_HOME = "${sdk}/libexec/android-sdk";
        ANDROID_SDK_ROOT = "${sdk}/libexec/android-sdk";
        GRADLE_OPTS = "-Dorg.gradle.project.android.aapt2FromMavenOverride=${sdk}/libexec/android-sdk/build-tools/35.0.0/aapt2";
        shellHook = ''
          export GRADLE_USER_HOME="$PWD/.gradle-user-home"
          export ANDROID_USER_HOME="$PWD/.android"
          export ANDROID_SDK_HOME="$PWD"
        '';
      };
    };
}
