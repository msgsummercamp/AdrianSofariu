export type DecodedToken = {
  sub: string;
  roles: string[];
  iat: number;
  exp: number;
};
